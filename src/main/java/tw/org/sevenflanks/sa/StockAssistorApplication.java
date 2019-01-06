package tw.org.sevenflanks.sa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
//@EntityScan(basePackageClasses = {StockAssistorApplication.class, Jsr310JpaConverters.class})
public class StockAssistorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockAssistorApplication.class, args);
	}

}
