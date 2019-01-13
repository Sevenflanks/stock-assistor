package tw.org.sevenflanks.sa.signal.dao;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.base.data.GenericEntity;
import tw.org.sevenflanks.sa.signal.entity.Signal;
import tw.org.sevenflanks.sa.signal.rule.Rule001;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
@Transactional
@Rollback
public class SignalDaoTest {

	@Autowired
	private SignalDao signalDao;

	@Autowired
	private Rule001 rule001;

	@Test
	public void test() {

		final int a = 5;
		final int b = 20;
		final Signal signal = new Signal("Test-01", "測試", rule001, Rule001.Factor.builder().a(a).b(b).build());
		final Signal saved = signalDao.save(signal);
		final Signal found = Optional.of(saved)
				.map(GenericEntity::getId)
				.flatMap(signalDao::findById)
				.orElseThrow(() -> new RuntimeException("entity not found"));

		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.getRuleCode()).isEqualTo(rule001.code());
		final Rule001.Factor factor = found.readFactor();
		Assertions.assertThat(factor).isNotNull();
		Assertions.assertThat(factor.getA()).isEqualTo(a);
		Assertions.assertThat(factor.getB()).isEqualTo(b);

	}

}
