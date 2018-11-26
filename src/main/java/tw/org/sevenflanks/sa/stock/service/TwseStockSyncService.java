package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;
import tw.org.sevenflanks.sa.stock.picker.TwseDataPicker;

@Service
@Transactional
public class TwseStockSyncService implements GenericSyncService<TwseStock, TwseStockDao> {

	@Autowired
	private TwseStockDao twseStockDao;

	@Autowired
	private TwseDataPicker twseDataPicker;

	@Override
	public TwseStockDao dao() {
		return twseStockDao;
	}

	@Override
	public List<TwseStock> fetch(LocalDate date) {
		// 公司別的API沒有日期輸入參數
		return twseDataPicker.getStockDay(date).getData5().stream()
				.map(TwseStock::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_stock";
	}
}
