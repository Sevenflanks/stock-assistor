package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.stock.model.DataStoringModel;

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

	public DataStoringModel syncAllToFileAndDb(LocalDate date) throws IOException {
		twseCompanySyncService.syncToFileAndDb(date);
		twseStockSyncService.syncToFileAndDb(date);
		twseRgremainSyncService.syncToFileAndDb(date);
		otcCompanySyncService.syncToFileAndDb(date);
		otcStockSyncService.syncToFileAndDb(date);
		otcRgremainSyncService.syncToFileAndDb(date);

		return checkDataStoreType(date);
	}

}
