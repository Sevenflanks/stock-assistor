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
import tw.org.sevenflanks.sa.signal.entity.SignalResult;
import tw.org.sevenflanks.sa.signal.model.SignalResultVo;
import tw.org.sevenflanks.sa.signal.model.SignalTask;
import tw.org.sevenflanks.sa.signal.model.SignalVo;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public List<SignalResult> get(Pageable pageable) {
		return signalResultDao.findByLastSyncDate(pageable);
	}

	/** 取得目前最新的結果 */
	public List<SignalResult> get() {
		return signalResultDao.findByLastSyncDate();
	}

	/** 補充資訊 */
	public void info(SignalResultVo signalResultVo) {
		final CompanyVo company = signalResultVo.getCompany();
		final LocalDate now = LocalDate.now();
		if (CompanyVo.TYPE_OTC.equals(company.getStockType())) {
			otcStockDao.findRecordDates(company.getUid(), now, 1).stream()
					.findFirst()
					.map(date -> otcStockDao.findByUidAndSyncDate(company.getUid(), date))
					.ifPresent(stock -> signalResultVo.setStock(StockVo.builder()
							.openingPrice(stock.getOpeningPrice())
							.closingPrice(stock.getClosingPrice())
							.upsDowns(stock.getUpsDowns())
							.highestPrice(stock.getHighestPrice())
							.lowestPrice(stock.getLowestPrice())
							.build()));

		} else if (CompanyVo.TYPE_TWSE.equals(company.getStockType())) {
			twseStockDao.findRecordDates(company.getUid(), now, 1).stream()
					.findFirst()
					.map(date -> twseStockDao.findByUidAndSyncDate(company.getUid(), date))
					.ifPresent(stock -> signalResultVo.setStock(StockVo.builder()
							.openingPrice(stock.getOpeningPrice())
							.closingPrice(stock.getClosingPrice())
							.upsDowns(stock.getUpsDowns())
							.highestPrice(stock.getHighestPrice())
							.lowestPrice(stock.getLowestPrice())
							.build()));

		}
	}

	/** 根據所有訊號設定跑出結果 */
	public Collection<SignalResult> run() {
		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();
		final ExecutorService executor = Executors.newCachedThreadPool();

		final SignalTask[] tasks = signalDao.findAll().stream()
				.map(signal -> {
					final SignalRule<?> rule = rules.get(signal.getRuleCode());
					final CompletableFuture<List<CompanyVo>> future = CompletableFuture.supplyAsync(() -> rule.getMatch(signal.readFactor(), otcCompanies, twseCompanies), executor);
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

	public Collection<SignalResult> runAndSave() {
		final Collection<SignalResult> results = this.run();

		final LocalDate now = LocalDate.now();
		results.forEach(result -> result.setSyncDate(now));
		signalResultDao.deleteBySyncDate(now);
		signalResultDao.saveAll(results);
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
