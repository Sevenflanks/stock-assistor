package tw.org.sevenflanks.sa.signal.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.base.data.JsonListModel;
import tw.org.sevenflanks.sa.base.data.JsonModel;
import tw.org.sevenflanks.sa.signal.dao.SignalDao;
import tw.org.sevenflanks.sa.signal.dao.SignalResultDao;
import tw.org.sevenflanks.sa.signal.entity.Signal;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;
import tw.org.sevenflanks.sa.signal.model.*;
import tw.org.sevenflanks.sa.signal.rule.SignalRule;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.OtcStockDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;
import tw.org.sevenflanks.sa.stock.model.StockVo;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SignalService {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private OtcStockDao otcStockDao;

	@Autowired
	private TwseStockDao twseStockDao;

	@Autowired
	private SignalDao signalDao;

	@Autowired
	private SignalResultDao signalResultDao;

	private Map<String, SignalRule<?>> rules;

	@Autowired
	public void setRules(List<SignalRule<?>> rules) {
		this.rules = rules.stream().collect(Collectors.toMap(SignalRule::code, Function.identity(), (o1, o2) -> {
			throw new RuntimeException("存在兩個以上相同代號的的rule: " + o1.code() + ":" + o1 + ", " + o2.code() + ":" + o2);
		}));
	}

	/** 取得目前最新的結果(分頁) */
	public List<SignalResult> get(LocalDate baseDate, Pageable pageable) {
		return signalResultDao.findBySyncDate(baseDate, pageable);
	}

	/** 取得目前最新的結果 */
	public List<SignalResult> get() {
		return signalResultDao.findByLastSyncDate();
	}

	/** 補充資訊 */
	public void info(SignalResultVo signalResultVo) {
		final CompanyVo company = signalResultVo.getCompany();
		final LocalDate baseDate = signalResultVo.getSyncDate();
		if (CompanyVo.TYPE_OTC.equals(company.getStockType())) {
			Optional.ofNullable(otcStockDao.findByUidAndSyncDate(company.getUid(), baseDate))
					.ifPresent(stock -> signalResultVo.setStock(StockVo.builder()
							.openingPrice(stock.getOpeningPrice())
							.closingPrice(stock.getClosingPrice())
							.upsDowns(stock.getUpsDowns())
							.highestPrice(stock.getHighestPrice())
							.lowestPrice(stock.getLowestPrice())
							.build()));

		} else if (CompanyVo.TYPE_TWSE.equals(company.getStockType())) {
			Optional.ofNullable(twseStockDao.findByUidAndSyncDate(company.getUid(), baseDate))
					.ifPresent(stock -> signalResultVo.setStock(StockVo.builder()
							.openingPrice(stock.getOpeningPrice())
							.closingPrice(stock.getClosingPrice())
							.upsDowns(stock.getUpsDowns())
							.highestPrice(stock.getHighestPrice())
							.lowestPrice(stock.getLowestPrice())
							.build()));

		}
	}

	/** 根據所有訊號設定跑出結果
	 * @param baseDate*/
	public Collection<SignalResult> run(LocalDate baseDate) {
		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();
		final ExecutorService executor = Executors.newWorkStealingPool();

		final SignalTask[] tasks = signalDao.findAll().stream()
                .sorted(Comparator.comparing(Signal::getRuleCode))
				.map(signal -> {
					final SignalProgress progress = SignalProgress.start(
							SignalProgress.RUN_SIGNAL_PRIFIX + baseDate,
							otcCompanies.size() + twseCompanies.size(),
							"訊號處理中:" + signal.getName());
					final SignalRunOption optional = SignalRunOption.builder()
							.beforeDoMatch(c -> progress.add(1))
							.build();

					final SignalRule<?> rule = rules.get(signal.getRuleCode());
					final CompletableFuture<List<CompanyVo>> future = CompletableFuture.supplyAsync(
							() -> rule.getMatch(baseDate, signal.readFactor(), otcCompanies, twseCompanies, optional)
									.collect(Collectors.toList())
							, executor);
					return new SignalTask(signal, future);
				})
				.toArray(SignalTask[]::new);

		final HashMap<String, SignalResult> result = new HashMap<>();
		try {
			for (SignalTask task : tasks) {
				toResult(result, task);
			}
		} catch (Exception e) {
			throw new RuntimeException("generate to result failed", e);
		}

		return result.values();
	}

	public Collection<SignalResult> runAndSave(LocalDate baseDate) {
		final Collection<SignalResult> results = this.run(baseDate);

		final SignalProgress progress = SignalProgress.start(SignalProgress.RUN_SIGNAL_PRIFIX + baseDate, 1, "結果儲存中");
		results.forEach(result -> result.setSyncDate(baseDate));
		signalResultDao.deleteBySyncDate(baseDate);
		signalResultDao.saveAll(results);
		progress.add(1);

		SignalProgress.finish(SignalProgress.RUN_SIGNAL_PRIFIX + baseDate);

		return results;
	}

	private void toResult(HashMap<String, SignalResult> result, SignalTask task) throws ExecutionException, InterruptedException {
		task.getFuture().get().forEach(c -> {
			if (result.containsKey(c.getUid())) {
				final SignalResult signalResult = result.get(c.getUid());
				signalResult.getMatchs().get().add(new SignalVo(task.getSignal()));
				signalResult.setSize(signalResult.getMatchs().get().size());
			} else {
				result.put(c.getUid(), new SignalResult(
						c.getUid(),
						1,
						JsonModel.<CompanyVo>builder().value(c).build(),
						JsonListModel.<SignalVo>builder().value(Lists.newArrayList(new SignalVo(task.getSignal()))).build())
				);
			}
		});
	}

}
