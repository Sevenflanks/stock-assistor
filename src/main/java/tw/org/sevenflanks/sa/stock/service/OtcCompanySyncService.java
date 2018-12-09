package tw.org.sevenflanks.sa.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.picker.OtcCompanyPicker;

@Service
@Transactional
public class OtcCompanySyncService implements GenericSyncService<OtcCompany, OtcCompanyDao> {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private OtcCompanyPicker otcCompanyPicker;

	@Override
	public OtcCompanyDao dao() {
		return otcCompanyDao;
	}

	@Override
	public Class<OtcCompany> entityClass() {
		return OtcCompany.class;
	}

	@Override
	public List<OtcCompany> fetch(LocalDate date) {
		// 公司別的API沒有日期輸入參數
		return otcCompanyPicker.getAll().stream()
				.map(OtcCompany::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "otc_company";
	}
}
