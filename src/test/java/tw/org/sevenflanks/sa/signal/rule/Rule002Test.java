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

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class Rule002Test {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private Rule002 rule002;

	@Test
	public void test() {
		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();

		rule002.getMatch(LocalDate.now(), Rule002.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies)
				.forEach(System.out::println);

		rule002.getMatch(LocalDate.now(), Rule002.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies)
				.forEach(System.out::println);
	}

}
