package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;
import tw.org.sevenflanks.sa.stock.picker.TwseCompanyPicker;
import tw.org.sevenflanks.sa.stock.picker.TwseDataPicker;

@Service
@Transactional
public class StockService {

	@Autowired
	private TwseCompanyPicker twseCompanyPicker;

	@Autowired
	private TwseDataPicker twseDataPicker;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private TwseStockDao twseStockDao;

	public void syncToFileAndDb(LocalDate localDate) throws IOException {
		final Path path = Paths.get("data").resolve(localDate.toString());
		// 若本機端還沒抓過資訊，則從遠端先取一次
		if (!Files.isDirectory(path)) {
			syncToFile(localDate);
		}
		loadToDb(localDate);
	}

	/** 從檔案中讀取 */
	public void loadToDb(LocalDate localDate) throws IOException {
		final Path path = Paths.get("data").resolve(localDate.toString());

		// 上市公司資料
		final Path twseCompany = path.resolve("twse_company");
		final List<TwseCompany> twseCompanies = objectMapper.readValue(twseCompany.toFile(), new TypeReference<List<TwseCompany>>(){});
		twseCompanies.forEach(e -> e.setSyncDate(localDate));
		twseCompanyDao.deleteBySyncDate(localDate);
		twseCompanyDao.saveAll(twseCompanies);

		// 上市股票行情
		final Path twseStock = path.resolve("twse_stock");
		final List<TwseStock> twseStocks = objectMapper.readValue(twseStock.toFile(), new TypeReference<List<TwseStock>>(){});
		twseStocks.forEach(e -> e.setSyncDate(localDate));
		twseStockDao.deleteBySyncDate(localDate);
		twseStockDao.saveAll(twseStocks);
	}

	/** 將API資料取回並寫入檔案 */
	public void syncToFile(LocalDate localDate) throws IOException {
		final Path path = Paths.get("data").resolve(localDate.toString());

		// 沒有資料夾的話先建立
		if (!Files.isDirectory(path)) {
			Files.createDirectory(path);
		}

		// 上市公司資料
		final Path twseCompany = path.resolve("twse_company");
		if (!Files.exists(twseCompany)) {
			final List<TwseCompany> twseCompanies = twseCompanyPicker.getAll().stream()
					.map(TwseCompany::new)
					.collect(Collectors.toList());
			objectMapper.writeValue(twseCompany.toFile(), twseCompanies);
		}

		// 上市股票行情
		final Path twseStock = path.resolve("twse_stock");
		if (!Files.exists(twseStock)) {
			final List<TwseStock> twseStocks = twseDataPicker.getStockDay(localDate).getData5().stream()
					.map(TwseStock::new)
					.collect(Collectors.toList());
			objectMapper.writeValue(twseStock.toFile(), twseStocks);
		}
	}

}
