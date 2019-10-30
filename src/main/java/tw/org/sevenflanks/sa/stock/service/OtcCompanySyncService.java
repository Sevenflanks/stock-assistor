package tw.org.sevenflanks.sa.stock.service;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.base.utils.SqlUtils;
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

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public OtcCompanyDao dao() {
		return otcCompanyDao;
	}

	@Override
	public int batchSave(LocalDate date, List<OtcCompany> datas) {
		final LocalDateTime saveDbStartTime = LocalDateTime.now();

		AtomicInteger result = new AtomicInteger();
		String sql = SqlUtils.batchInsert(OtcCompany.class);
		Iterables.partition(datas, 1000).forEach(partition -> {
			MapSqlParameterSource[] batchValuesMap = SqlUtils.getBatchValuesToMap(partition, data -> data.setSyncDate(date));
			result.addAndGet(IntStream.of(this.jdbcTemplate.batchUpdate(sql, batchValuesMap)).sum());
		});

		log.info("[{}@{}] saved to db, in {}s", this.zhName(), date, ChronoUnit.SECONDS.between(saveDbStartTime, LocalDateTime.now()));
		return result.get();
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
