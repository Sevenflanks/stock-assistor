package tw.org.sevenflanks.sa.stock.picker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.utils.WebFluxUtils;
import tw.org.sevenflanks.sa.stock.model.OtcExchangeModel;

import java.time.LocalDate;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class OtcRgremainPicker {

	private static final DateTimeFormatter API_DATE = DateTimeFormatter.ofPattern("yyy/MM/dd");

	/** 融資餘額(櫃買中心) */
	@Retryable(maxAttempts = 3, backoff = @Backoff(value = 5000))
	public OtcExchangeModel getStockDay(LocalDate date) {

		// 建立request
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.tpex.org.tw/web/stock/margin_trading/margin_sbl/margin_sbl_result.php")
				.queryParam("l", "zh-tw")
				.queryParam("d", API_DATE.format(MinguoDate.from(date))); // 輸入參數的日期範例 107/12/07
		final RestTemplate restTemplate = new RestTemplate(WebFluxUtils.SSL());
		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<?> request = new HttpEntity<>(headers);
		final String url = builder.toUriString();
		final ResponseEntity<OtcExchangeModel> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				OtcExchangeModel.class);

		return MsgTemplate.requireBody("櫃買中心API", url, response);
	}
}
