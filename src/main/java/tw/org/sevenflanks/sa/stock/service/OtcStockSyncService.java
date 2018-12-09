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

import tw.org.sevenflanks.sa.stock.dao.OtcStockDao;
import tw.org.sevenflanks.sa.stock.entity.OtcStock;
import tw.org.sevenflanks.sa.stock.model.OtcStockModel;
import tw.org.sevenflanks.sa.stock.picker.OtcStockPicker;

@Service
@Transactional
public class OtcStockSyncService implements GenericSyncService<OtcStock, OtcStockDao> {

	@Autowired
	private OtcStockDao otcStockDao;

	@Autowired
	private OtcStockPicker otcStockPicker;

	@Override
	public OtcStockDao dao() {
		return otcStockDao;
	}

	@Override
	public Class<OtcStock> entityClass() {
		return OtcStock.class;
	}

	@Override
	public List<OtcStock> fetch(LocalDate date) {
		final OtcStockModel stockDay = otcStockPicker.getStockDay(date);
		return Stream.concat(
				Optional.ofNullable(stockDay).map(OtcStockModel::getMmData).map(Collection::stream).orElseGet(Stream::empty),
				Optional.ofNullable(stockDay).map(OtcStockModel::getAaData).map(Collection::stream).orElseGet(Stream::empty))
				.map(OtcStock::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "otc_stock";
	}

	@Override
	public String zhName() {
		return "上櫃股票行情";
	}
}
