package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockService {
	
	@Autowired
	private TwseCompanySyncService twseCompanySyncService;

	@Autowired
	private TwseStockSyncService twseStockSyncService;

	@Autowired
	private TwseRgremainSyncService twseRgremainSyncService;

	public void syncAllToFileAndDb(LocalDate date) throws IOException {
		twseCompanySyncService.syncToFileAndDb(date);
		twseStockSyncService.syncToFileAndDb(date);
		twseRgremainSyncService.syncToFileAndDb(date);
	}

}
