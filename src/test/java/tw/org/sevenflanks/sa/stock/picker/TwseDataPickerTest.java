package tw.org.sevenflanks.sa.stock.picker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tw.org.sevenflanks.sa.stock.model.TwseExchangeModel;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TwseDataPickerTest {

	@Autowired
	private TwseDataPicker twseDataPicker;

	@Test
	public void test() {
//		final TwseDailyModel stockDay = twseDataPicker.getStockDay(LocalDate.of(2019, 1, 30));
//
//		stockDay.getFields9().forEach(f -> System.out.print(f + " "));
//		System.out.println();
//		System.out.println();
//
//		stockDay.getData9().forEach(System.out::println);


		final TwseExchangeModel rgremain = twseDataPicker.getRgremain(LocalDate.of(2018, 11, 23));

		rgremain.getData().forEach(f -> System.out.print(f + " "));
		System.out.println();
		System.out.println();

		rgremain.getData().forEach(System.out::println);
	}

}
