package tw.org.sevenflanks.sa.signal.rule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
public class Sng001Test {

	@Autowired
	private Sng001 sng001;

	@Test
	public void test() {
		sng001.getMatch(Sng001.Factor.builder().a(5).b(5).build())
				.forEach(System.out::println);

		sng001.getMatch(Sng001.Factor.builder().a(5).b(20).build())
				.forEach(System.out::println);
	}

}
