package tw.org.sevenflanks.sa.stock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.stock.model.DataStoringModel;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
@Transactional
public class StockService {
	
	@Autowired
	private TwseCompanySyncService twseCompanySyncService;

	@Autowired
	private TwseStockSyncService twseStockSyncService;

	@Autowired
	private TwseRgremainSyncService twseRgremainSyncService;

	@Autowired
	private OtcCompanySyncService otcCompanySyncService;

	@Autowired
	private OtcStockSyncService otcStockSyncService;

	@Autowired
	private OtcRgremainSyncService otcRgremainSyncService;

	public DataStoringModel checkDataStoreType(LocalDate date) {
		return DataStoringModel.builder()
				.dataDate(date)
				.otcCompany(otcCompanySyncService.check(date))
				.otcRgremain(otcRgremainSyncService.check(date))
				.otcStock(otcStockSyncService.check(date))
				.twseCompany(twseCompanySyncService.check(date))
				.twseRgremain(twseRgremainSyncService.check(date))
				.twseStock(twseStockSyncService.check(date))
				.build();
	}

	public DataStoringModel syncAll(LocalDate date) throws IOException {
		twseStockSyncService.sync(date, true);
		twseRgremainSyncService.sync(date, true);
		otcStockSyncService.sync(date, true);
		otcRgremainSyncService.sync(date, true);

		twseCompanySyncService.syncOnlyLatest(date, true);
		otcCompanySyncService.syncOnlyLatest(date, true);

		return checkDataStoreType(date);
	}

	public DataStoringModel syncAllFromFile(LocalDate date) throws IOException {
		twseStockSyncService.sync(date, false);
		twseRgremainSyncService.sync(date, false);
		otcStockSyncService.sync(date, false);
		otcRgremainSyncService.sync(date, false);

		twseCompanySyncService.syncOnlyLatest(date, false);
		otcCompanySyncService.syncOnlyLatest(date, false);

		return checkDataStoreType(date);
	}

}
