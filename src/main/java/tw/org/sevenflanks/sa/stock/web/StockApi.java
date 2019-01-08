package tw.org.sevenflanks.sa.stock.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import tw.org.sevenflanks.sa.base.msg.enums.MsgLevel;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.base.msg.model.MsgBody;
import tw.org.sevenflanks.sa.base.utils.WebFluxUtils;
import tw.org.sevenflanks.sa.stock.model.DataStoringModel;
import tw.org.sevenflanks.sa.stock.service.StockService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/stock")
public class StockApi {

	@Autowired
	private StockService stockService;

	@GetMapping("/check/year/{year}")
	public ParallelFlux<ServerSentEvent<DataStoringModel>> check(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Year year) {
		final Iterator<LocalDate> dates = toDateIterator(
				min(LocalDate.now(), year.atDay(1).with(TemporalAdjusters.lastDayOfYear())),
				year.atDay(1));
		return WebFluxUtils.SSE(Flux.fromIterable(() -> dates)
				.parallel()
				.map(stockService::checkDataStoreType));
	}

	@GetMapping("/init/{type}/month/{yearMonth}")
	public ParallelFlux<ServerSentEvent<DataStoringModel>> init(@PathVariable String type, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth yearMonth) {
		final Iterator<LocalDate> dates = toDateIterator(
				min(LocalDate.now(), yearMonth.atEndOfMonth()),
				yearMonth.atDay(1));
		return WebFluxUtils.SSE(Flux.fromIterable(() -> dates)
				.parallel()
				.map(date -> {
					try {
						if ("api".equals(type)) {
							return stockService.syncAll(date);
						} else if ("file".equals(type)) {
							return stockService.syncAllFromFile(date);
						} else {
							throw new MsgException(MsgTemplate.API9999.build("不明的資料初始化類型: {0}", type));
						}
					} catch (MsgException e) {
						if (e.getMsg().getLevel().isGE(MsgLevel.IMPORTANT)) {
							log.info("[{}] syncAll failed: {}", date, e.getMsg());
							return DataStoringModel.error(date, e);
						} else {
							log.info("[{}] syncAll failed", date, e);
						}
						return DataStoringModel.error(date, e);
					} catch (Exception e) {
						log.info("[{}] syncAll failed", date, e);
						return DataStoringModel.error(date, e);
					}
				}));
	}

	@GetMapping("/init/{date}")
	public ResponseEntity<MsgBody<DataStoringModel>> init(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
		return MsgBody.ok(stockService.syncAll(date));
	}

	private Iterator<LocalDate> toDateIterator(LocalDate start, LocalDate end) {
		return new Iterator<LocalDate>() {
			private LocalDate currDate = null;

			@Override
			public boolean hasNext() {
				return currDate == null || currDate.isAfter(end);
			}

			@Override
			public LocalDate next() {
				currDate = Optional.ofNullable(currDate).map(d -> d.minusDays(1)).orElse(start);
				return currDate;
			}
		};
	}

	private LocalDate min(LocalDate d1, LocalDate d2) {
		return d1.isBefore(d2) ? d1 : d2;
	}

}
