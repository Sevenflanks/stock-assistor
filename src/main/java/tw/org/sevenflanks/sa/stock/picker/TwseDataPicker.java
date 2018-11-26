package tw.org.sevenflanks.sa.stock.picker;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.stock.model.TwseExchangeModel;
import tw.org.sevenflanks.sa.stock.model.TwseDailyModel;

@Slf4j
@Component
public class TwseDataPicker {

	private static final DateTimeFormatter API_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

	/** 個股成交資訊(證交所) */
	public TwseDailyModel getStockDay(LocalDate date) {

		// 建立request
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://www.twse.com.tw/exchangeReport/MI_INDEX")
				.queryParam("response", "json")
				.queryParam("date", date.format(API_DATE))
				.queryParam("type", "ALL");
		final RestTemplate restTemplate = new RestTemplate();
		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<?> request = new HttpEntity<>(headers);
		final String url = builder.toUriString();
		final ResponseEntity<TwseDailyModel> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				TwseDailyModel.class);

		return MsgTemplate.requireBody("證交所API", url, response);
	}

	/** 上市融券餘額資訊(證交所) */
	public TwseExchangeModel getRgremain(LocalDate date) {
		final long milli = Instant.now().toEpochMilli();
		final String paramResponse = "json";
		final String paramDate = date.format(API_DATE);
		final String paramMilli = Long.toString(milli);
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://www.twse.com.tw/exchangeReport/TWT93U")
				.queryParam("response", paramResponse)
				.queryParam("date", paramDate)
				.queryParam("_", paramMilli);

		return httpGetBody(builder);
	}

	private TwseExchangeModel httpGetBody(UriComponentsBuilder builder) {
		final RestTemplate restTemplate = new RestTemplate();
		final HttpHeaders headers = new HttpHeaders();
		final HttpEntity<?> request = new HttpEntity<>(headers);
		headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON_UTF8, MediaType.APPLICATION_JSON));
		headers.setHost(InetSocketAddress.createUnresolved("www.twse.com.tw", 80)); // 測試結果這是非必要

		final String url = builder.toUriString();
		final ResponseEntity<TwseExchangeModel> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				request,
				TwseExchangeModel.class);

		return MsgTemplate.requireBody("證交所API", url, response);
	}
}
