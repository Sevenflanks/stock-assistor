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
				.otcCompany(otcCompanySyncService.checkDataStoreType(date))
				.otcRgremain(otcRgremainSyncService.checkDataStoreType(date))
				.otcStock(otcStockSyncService.checkDataStoreType(date))
				.twseCompany(twseCompanySyncService.checkDataStoreType(date))
				.twseRgremain(twseRgremainSyncService.checkDataStoreType(date))
				.twseStock(twseStockSyncService.checkDataStoreType(date))
				.build();
	}

	public DataStoringModel syncAll(LocalDate date) throws IOException {
		twseCompanySyncService.sync(date, true);
		twseStockSyncService.sync(date, true);
		twseRgremainSyncService.sync(date, true);
		otcCompanySyncService.sync(date, true);
		otcStockSyncService.sync(date, true);
		otcRgremainSyncService.sync(date, true);

		return checkDataStoreType(date);
	}

	public DataStoringModel syncAllFromFile(LocalDate date) throws IOException {
		twseCompanySyncService.sync(date, false);
		twseStockSyncService.sync(date, false);
		twseRgremainSyncService.sync(date, false);
		otcCompanySyncService.sync(date, false);
		otcStockSyncService.sync(date, false);
		otcRgremainSyncService.sync(date, false);

		return checkDataStoreType(date);
	}

}
