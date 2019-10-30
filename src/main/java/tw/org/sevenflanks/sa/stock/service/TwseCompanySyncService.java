package tw.org.sevenflanks.sa.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.picker.TwseCompanyPicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class TwseCompanySyncService implements GenericSyncService<TwseCompany, TwseCompanyDao> {

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private TwseCompanyPicker twseCompanyPicker;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public TwseCompanyDao dao() {
		return twseCompanyDao;
	}

	@Override
	public int batchSave(LocalDate date, List<TwseCompany> datas) {
    return 0;
  }

	@Override
	public Class<TwseCompany> entityClass() {
		return TwseCompany.class;
	}

	public void syncOnlyLatest(LocalDate date, boolean fetchFromApi) throws IOException {
		LocalDate lastSyncDate = twseCompanyDao.findLastSyncDate();
		// 公司資料取最新就好，如果目標日期小於最後有資料的日期就不用再跑一次同步了
		if (lastSyncDate != null && lastSyncDate.isAfter(date)) {
			log.info("[{}@{}] already have newer data:{}, skip sync", this.zhName(), date, lastSyncDate);
		} else {
			GenericSyncService.super.sync(date, fetchFromApi);
		}
	}

	@Override
	public List<TwseCompany> fetch(LocalDate date) {
		// 公司別的API沒有日期輸入參數
		return Optional.ofNullable(twseCompanyPicker.getAll())
				.map(Collection::stream)
				.orElseGet(Stream::empty)
				.map(TwseCompany::new)
				.collect(Collectors.toList());
	}

	@Override
	public String fileName() {
		return "twse_company";
	}

	@Override
	public String zhName() {
		return "上市公司資訊";
	}
}
