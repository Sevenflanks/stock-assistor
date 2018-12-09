package tw.org.sevenflanks.sa.stock.picker;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import tw.org.sevenflanks.sa.stock.model.OtcStockModel;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OtcStockPickerTest {

	@Autowired OtcStockPicker otcStockPicker;

	@Test
	public void test() {

		final OtcStockModel stockDay = otcStockPicker.getStockDay(LocalDate.of(2018, 11, 23));
		stockDay.getMmData().forEach(System.out::println);
		System.out.println("==================================================");
		stockDay.getAaData().forEach(System.out::println);

	}

}
