package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;
import tw.org.sevenflanks.sa.stock.model.TwseDailyModel;
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
		return Optional.ofNullable(twseDataPicker.getStockDay(date))
				.map(TwseDailyModel::getData5)
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(TwseStock::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_stock";
	}

	@Override
	public String zhName() {
		return "上市股票行情";
	}
}
