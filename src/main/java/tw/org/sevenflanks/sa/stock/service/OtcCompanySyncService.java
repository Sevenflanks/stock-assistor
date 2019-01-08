package tw.org.sevenflanks.sa.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.picker.OtcCompanyPicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public void syncOnlyLatest(LocalDate date, boolean fetchFromApi) throws IOException {
		LocalDate lastSyncDate = otcCompanyDao.findLastSyncDate();
		// 公司資料取最新就好，如果目標日期小於最後有資料的日期就不用再跑一次同步了
		if (lastSyncDate != null && lastSyncDate.isAfter(date)) {
			log.info("[{}@{}] already have newer data:{}, skip sync", this.zhName(), date, lastSyncDate);
		} else {
			GenericSyncService.super.sync(date, fetchFromApi);
		}
	}

	@Override
	public List<OtcCompany> fetch(LocalDate date) {
		// 公司別的API沒有日期輸入參數
		return Optional.ofNullable(otcCompanyPicker.getAll())
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(OtcCompany::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "otc_company";
	}

	@Override
	public String zhName() {
		return "上櫃公司資訊";
	}
}
