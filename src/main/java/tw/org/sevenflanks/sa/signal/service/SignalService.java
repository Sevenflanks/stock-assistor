package tw.org.sevenflanks.sa.signal.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.org.sevenflanks.sa.signal.dao.SignalDao;
import tw.org.sevenflanks.sa.signal.model.SigalResult;
import tw.org.sevenflanks.sa.signal.model.SignalTask;
import tw.org.sevenflanks.sa.signal.rule.SignalRule;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

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
public class SignalService {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private SignalDao signalDao;

	private Map<String, SignalRule<?>> rules;

	@Autowired
	public void setRules(List<SignalRule<?>> rules) {
		this.rules = rules.stream().collect(Collectors.toMap(SignalRule::code, Function.identity(), (o1, o2) -> {
			throw new RuntimeException("存在兩個以上相同代號的的rule: " + o1.code() + ":" + o1 + ", " + o2.code() + ":" + o2);
		}));
	}

	public Collection<SigalResult> run() {

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

		final HashMap<String, SigalResult> result = new HashMap<>();
		try {
			for (SignalTask task : tasks) {
				toResult(result, task);
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}

		return result.values();
	}

	private void toResult(HashMap<String, SigalResult> result, SignalTask task) throws ExecutionException, InterruptedException {
		task.getFuture().get().forEach(c -> {
			if (result.containsKey(c.getUid())) {
				result.get(c.getUid()).getMatchs().add(task.getSignal());
			} else {
				result.put(c.getUid(), new SigalResult(c, Lists.newArrayList(task.getSignal())));
			}
		});
	}

}
