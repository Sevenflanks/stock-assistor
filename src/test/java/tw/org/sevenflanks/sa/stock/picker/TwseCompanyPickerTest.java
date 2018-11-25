package tw.org.sevenflanks.sa.stock.picker;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TwseCompanyPickerTest {

	@Autowired
	private TwseCompanyPicker twseCompanyPicker;

	@Test
	public void getAll() {
		twseCompanyPicker.getAll()
				.stream()
				.peek(m -> {
					Assertions.assertThat(m.getUid()).isNotBlank();
					Assertions.assertThat(m.getFullName()).isNotBlank();
				})
				.forEach(System.out::println);
	}
}
