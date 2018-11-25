package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StockServiceTest {

	@Autowired
	private StockService stockService;

	@Test
	public void test() throws IOException {
		stockService.syncToFileAndDb(LocalDate.of(2018, 11, 23));
	}

}
