package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.TwseRgremainDao;
import tw.org.sevenflanks.sa.stock.entity.TwseRgremain;
import tw.org.sevenflanks.sa.stock.picker.TwseDataPicker;

@Service
@Transactional
public class TwseRgremainSyncService implements GenericSyncService<TwseRgremain, TwseRgremainDao> {

	@Autowired
	private TwseRgremainDao twseStockDao;

	@Autowired
	private TwseDataPicker twseDataPicker;

	@Override
	public TwseRgremainDao dao() {
		return twseStockDao;
	}

	@Override
	public Class<TwseRgremain> entityClass() {
		return TwseRgremain.class;
	}

	@Override
	public List<TwseRgremain> fetch(LocalDate date) {
		return twseDataPicker.getRgremain(date).getData().stream()
				.map(TwseRgremain::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_rgremain";
	}
}
