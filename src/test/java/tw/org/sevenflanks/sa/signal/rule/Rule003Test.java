package tw.org.sevenflanks.sa.signal.rule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import tw.org.sevenflanks.sa.signal.model.SignalRunOption;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class Rule003Test {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private Rule003 rule003;

	@Test
	public void test() {
		final List<OtcCompany> otcCompanies = otcCompanyDao.findByLastSyncDate();
		final List<TwseCompany> twseCompanies = twseCompanyDao.findByLastSyncDate();

		rule003.getMatch(LocalDate.now(), Rule003.Factor.builder().a(5).b(5).build(), otcCompanies, twseCompanies, new SignalRunOption())
				.forEach(System.out::println);

		rule003.getMatch(LocalDate.now(), Rule003.Factor.builder().a(5).b(20).build(), otcCompanies, twseCompanies, new SignalRunOption())
				.forEach(System.out::println);
	}

}
