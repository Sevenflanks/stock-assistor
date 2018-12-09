package tw.org.sevenflanks.sa.stock.picker;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OtcRgremainPickerTest {

	@Autowired
	private OtcRgremainPicker otcRgremainPicker;

	@Test
	public void test() {
		otcRgremainPicker.getStockDay(LocalDate.of(2018, 12, 7)).getAaData()
				.forEach(System.out::println);
	}

}
