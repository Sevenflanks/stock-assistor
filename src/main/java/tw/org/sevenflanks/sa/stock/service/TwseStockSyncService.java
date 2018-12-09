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
	public Class<TwseStock> entityClass() {
		return TwseStock.class;
	}

	@Override
	public List<TwseStock> fetch(LocalDate date) {
		return twseDataPicker.getStockDay(date).getData5().stream()
				.map(TwseStock::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_stock";
	}
}
