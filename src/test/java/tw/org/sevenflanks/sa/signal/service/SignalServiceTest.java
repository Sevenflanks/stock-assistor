package tw.org.sevenflanks.sa.signal.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.signal.dao.SignalDao;
import tw.org.sevenflanks.sa.signal.dao.SignalResultDao;
import tw.org.sevenflanks.sa.signal.entity.Signal;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;
import tw.org.sevenflanks.sa.signal.model.SignalVo;
import tw.org.sevenflanks.sa.signal.rule.Rule001;
import tw.org.sevenflanks.sa.signal.rule.Rule002;
import tw.org.sevenflanks.sa.signal.rule.Rule003;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class SignalServiceTest {

	@Autowired
	private SignalDao signalDao;

	@Autowired
	private SignalResultDao signalResultDao;

	@Autowired
	private SignalService signalService;

	@Autowired
	private Rule001 rule001;

	@Autowired
	private Rule002 rule002;

	@Autowired
	private Rule003 rule003;

	@Test
	@Transactional
	@Rollback
	public void run() {
		final Function<SignalResult, String> byUid = r -> r.getCompany().get().getUid();
		final Function<SignalResult, Integer> bySize = r -> r.getMatchs().get().size();

		final List<SignalResult> results = signalService.run().stream()
				.sorted(Comparator.comparing(bySize, Comparator.reverseOrder()).thenComparing(byUid))
				.collect(Collectors.toList());

		results.forEach(result -> {
					System.out.println("符合" + result.getMatchs().get().size() + "項 " + result.getCompany().get() + ": " + result.getMatchs().get().stream().map(SignalVo::getShortName).sorted(Comparator.naturalOrder()).collect(Collectors.joining(",")));
				});

		final LocalDate now = LocalDate.now();
		results.forEach(result -> result.setSyncDate(now));
		signalResultDao.deleteBySyncDate(now);
		signalResultDao.saveAll(results);
	}

	@Test
	@Transactional
	@Commit
	public void init() {
		signalDao.deleteAll();
		signalDao.save(new Signal("R001SNG01", "成交量連續 5 個營業日低於近 5 個營業日平均值", "成交量5/5", rule001, Rule001.Factor.builder().a(5).b(5).build()));
		signalDao.save(new Signal("R001SNG02", "成交量連續 5 個營業日低於近 20 個營業日平均值", "成交量5/20", rule001, Rule001.Factor.builder().a(5).b(20).build()));
		signalDao.save(new Signal("R002SNG01", "融券餘額連續 5 個營業日低於近 5 個營業日平均值", "融券餘5/5", rule002, Rule002.Factor.builder().a(5).b(5).build()));
		signalDao.save(new Signal("R002SNG02", "融券餘額連續 5 個營業日低於近 20 個營業日平均值", "融券餘5/20", rule002, Rule002.Factor.builder().a(5).b(20).build()));
		signalDao.save(new Signal("R003SNG01", "融資餘額連續 5 個營業日低於近 5 個營業日平均值", "融資餘5/5", rule003, Rule003.Factor.builder().a(5).b(5).build()));
		signalDao.save(new Signal("R003SNG02", "融資餘額連續 5 個營業日低於近 20 個營業日平均值", "融資餘5/20", rule003, Rule003.Factor.builder().a(5).b(20).build()));
	}

}
