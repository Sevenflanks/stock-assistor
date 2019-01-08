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
public class Sng003Test {

	@Autowired
	private Sng003 sng003;

	@Test
	public void test() {
		sng003.getMatch(Sng003.Factor.builder().a(5).b(5).build())
				.forEach(System.out::println);

		sng003.getMatch(Sng003.Factor.builder().a(5).b(20).build())
				.forEach(System.out::println);
	}

}
