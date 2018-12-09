package tw.org.sevenflanks.sa.stock.picker;

import java.time.LocalDate;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.stock.model.OtcStockModel;

@Slf4j
@Component
public class OtcStockPicker {

	private static final DateTimeFormatter API_DATE = DateTimeFormatter.ofPattern("yyy/MM/dd");

	/** 個股成交資訊(櫃買中心) */
	@Retryable(maxAttempts = 3, backoff = @Backoff(value = 5000))
	public OtcStockModel getStockDay(LocalDate date) {

		// 建立request
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://www.tpex.org.tw/web/stock/aftertrading/daily_close_quotes/stk_quote_result.php")
				.queryParam("l", "zh-tw")
				.queryParam("o", "json")
				.queryParam("d", API_DATE.format(MinguoDate.from(date))) // 輸入參數的日期範例 107/12/07
				.queryParam("s", "0,asc,0");
		final RestTemplate restTemplate = new RestTemplate();
		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<?> request = new HttpEntity<>(headers);
		final String url = builder.toUriString();
		final ResponseEntity<OtcStockModel> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				OtcStockModel.class);

		return MsgTemplate.requireBody("櫃買中心API", url, response);
	}
}
