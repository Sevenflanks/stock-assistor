package tw.org.sevenflanks.sa.stock.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import tw.org.sevenflanks.sa.base.msg.model.MsgBody;
import tw.org.sevenflanks.sa.base.utils.WebFluxUtils;
import tw.org.sevenflanks.sa.stock.model.DataStoringModel;
import tw.org.sevenflanks.sa.stock.service.StockService;

@Slf4j
@RestController
@RequestMapping("/api/stock")
public class StockApi {

	@Autowired
	private StockService stockService;

	@GetMapping("/check/year/{year}")
	public Flux<ServerSentEvent<DataStoringModel>> check(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Year year) {
		final Iterator<LocalDate> dates = toDateIterator(year.atDay(1), min(LocalDate.now(), year.plusYears(1).atDay(1).minusDays(1)));
		return WebFluxUtils.SSE(Flux.fromIterable(() -> dates)
				.map(stockService::checkDataStoreType));
	}

	@GetMapping("/init/month/{yearMonth}")
	public Flux<ServerSentEvent<DataStoringModel>> init(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth yearMonth) {
		final Iterator<LocalDate> dates = toDateIterator(yearMonth.atDay(1), min(LocalDate.now(), yearMonth.atEndOfMonth()));
		return WebFluxUtils.SSE(Flux.fromIterable(() -> dates)
				.map(date -> {
					try {
						return stockService.syncAllToFileAndDb(date);
					} catch (Exception e) {
						log.info("[{}] syncAllToFileAndDb failed" , date, e);
						return DataStoringModel.error(date, e);
					}
				}));
	}

	@GetMapping("/init/{date}")
	public ResponseEntity<MsgBody<DataStoringModel>> init(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
		return MsgBody.ok(stockService.syncAllToFileAndDb(date));
	}

	private Iterator<LocalDate> toDateIterator(LocalDate firstDate, LocalDate lastDate) {
		return new Iterator<LocalDate>() {
			private LocalDate currDate = null;

			@Override
			public boolean hasNext() {
				return currDate == null || currDate.isBefore(lastDate);
			}

			@Override
			public LocalDate next() {
				currDate = Optional.ofNullable(currDate).map(d -> d.plusDays(1)).orElse(firstDate);
				return currDate;
			}
		};
	}

	private LocalDate min(LocalDate d1, LocalDate d2) {
		return d1.isBefore(d2) ? d1 : d2;
	}

}
