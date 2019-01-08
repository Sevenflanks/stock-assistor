package tw.org.sevenflanks.sa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class StockAssistorApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockAssistorApplication.class, args);
	}

}
