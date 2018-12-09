package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.OtcRgremainDao;
import tw.org.sevenflanks.sa.stock.entity.OtcRgremain;
import tw.org.sevenflanks.sa.stock.picker.OtcRgremainPicker;

@Service
@Transactional
public class OtcRgremainSyncService implements GenericSyncService<OtcRgremain, OtcRgremainDao> {

	@Autowired
	private OtcRgremainDao otcStockDao;

	@Autowired
	private OtcRgremainPicker otcRgremainPicker;

	@Override
	public OtcRgremainDao dao() {
		return otcStockDao;
	}

	@Override
	public Class<OtcRgremain> entityClass() {
		return OtcRgremain.class;
	}

	@Override
	public List<OtcRgremain> fetch(LocalDate date) {
		return otcRgremainPicker.getStockDay(date).getAaData().stream()
				.map(OtcRgremain::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "otc_rgremain";
	}
}
