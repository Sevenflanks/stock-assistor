package tw.org.sevenflanks.sa.signal.rule;


import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class CompositeTest {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private Rule001 rule001;

	@Autowired
	private Rule002 rule002;

	@Autowired
	private Rule003 rule003;

	@Test
	public void test() throws ExecutionException, InterruptedException {

		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();

		final ForkJoinTask<List<CompanyVo>> task1 = ForkJoinPool.commonPool().submit(() -> rule001.getMatch(Rule001.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies));
		final ForkJoinTask<List<CompanyVo>> task2 = ForkJoinPool.commonPool().submit(() -> rule001.getMatch(Rule001.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies));
		final ForkJoinTask<List<CompanyVo>> task3 = ForkJoinPool.commonPool().submit(() -> rule002.getMatch(Rule002.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies));
		final ForkJoinTask<List<CompanyVo>> task4 = ForkJoinPool.commonPool().submit(() -> rule002.getMatch(Rule002.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies));
		final ForkJoinTask<List<CompanyVo>> task5 = ForkJoinPool.commonPool().submit(() -> rule003.getMatch(Rule003.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies));
		final ForkJoinTask<List<CompanyVo>> task6 = ForkJoinPool.commonPool().submit(() -> rule003.getMatch(Rule003.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies));

		final List<CompanyVo> matchSng001_1 = task1.get();
		final List<CompanyVo> matchSng001_2 = task2.get();
		final List<CompanyVo> matchSng002_1 = task3.get();
		final List<CompanyVo> matchSng002_2 = task4.get();
		final List<CompanyVo> matchSng003_1 = task5.get();
		final List<CompanyVo> matchSng003_2 = task6.get();

		final HashMap<CompanyVo, List<String>> result = new HashMap<>();

		addToResult(result, matchSng001_1, "成交量5/5");
		addToResult(result, matchSng001_2, "成交量5/20");
		addToResult(result, matchSng002_1, "融券餘5/5");
		addToResult(result, matchSng002_2, "融券餘5/20");
		addToResult(result, matchSng003_1, "融資餘5/5");
		addToResult(result, matchSng003_2, "融資餘5/20");

		result.entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getValue().size(), Comparator.reverseOrder()))
				.forEach(e -> {
					System.out.println("符合" + e.getValue().size() + "項 " + e.getKey() + ": " + e.getValue().stream().sorted(Comparator.naturalOrder()).collect(Collectors.joining(",")));
				});
	}

	private void addToResult(HashMap<CompanyVo, List<String>> result, List<CompanyVo> matchSng001_1, String ruleName) {
		matchSng001_1.forEach(c -> {
			if (result.containsKey(c)) {
				result.get(c).add(ruleName);
			} else {
				result.put(c, Lists.newArrayList(ruleName));
			}
		});
	}

}
