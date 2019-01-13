package tw.org.sevenflanks.sa.signal.rule;

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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class Rule001Test {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private Rule001 rule001;

	@Test
	public void test() {
		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();

		rule001.getMatch(Rule001.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies)
				.forEach(System.out::println);

		rule001.getMatch(Rule001.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies)
				.forEach(System.out::println);
	}

}
