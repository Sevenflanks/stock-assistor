package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;

@Service
@Transactional
public class StockService {
	
	@Autowired
	private TwseCompanySyncService twseCompanySyncService;

	@Autowired
	private TwseStockSyncService twseStockSyncService;

	public void syncToFileAndDb(LocalDate date) throws IOException {
		final List<TwseCompany> twseCompanies = twseCompanySyncService.loadFromFile(date).orElseGet(() -> twseCompanySyncService.fetch(date));
		final List<TwseStock> twseStocks = twseStockSyncService.loadFromFile(date).orElseGet(() -> twseStockSyncService.fetch(date));

		twseCompanySyncService.saveToFile(date, twseCompanies);
		twseStockSyncService.saveToFile(date, twseStocks);

		twseCompanySyncService.saveToDb(date, twseCompanies);
		twseStockSyncService.saveToDb(date, twseStocks);
	}

}
