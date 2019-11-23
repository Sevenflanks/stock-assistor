package tw.org.sevenflanks.sa.stock.service;

import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import tw.org.sevenflanks.sa.base.exception.NotImplementedException;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.base.utils.SqlUtils;
import tw.org.sevenflanks.sa.config.GlobalConstants;
import tw.org.sevenflanks.sa.stock.dao.SyncDateDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;
import tw.org.sevenflanks.sa.stock.enums.DataStoreType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public abstract class AbstractSyncService<T extends SyncDateEntity, DAO extends SyncDateDao<T>> {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	abstract DAO dao();

	abstract Class<T> entityClass();

	/** 從遠端抓資料 */
	abstract List<T> fetch(LocalDate date);

	/** 檔案名稱 */
	abstract String fileName();

	/** 中文名稱 */
	abstract String zhName();

	/** 檢查資料的儲存類型 */
	DataStoreType check(LocalDate date) {
		try {
			final long dbCount = dao().countBySyncDate(date);
			if (dbCount > 0) {
				return DataStoreType.DB;
			}

			final Path path = Paths.get("data").resolve(YearMonth.from(date).toString()).resolve(date.toString());
			final Path loadFrom = path.resolve(fileName());
			if (Files.exists(loadFrom)) {
				return DataStoreType.FILE;
			}
		} catch (Exception e) {
			log.error("[{}@{}] checking DataStoreType failed", this.zhName(), date, e);
			return DataStoreType.FAILED;
		}

		return DataStoreType.NONE;
	}

	/** 與遠端API或File同步資料到Sql */
	void sync(LocalDate date, boolean fetchFromApi) throws IOException {
		// 先從檔案資料讀取，沒有的話再從Api撈
		final LocalDateTime fetchFileStartTime = LocalDateTime.now();
		final Optional<List<T>> fileData = this.loadFromFile(date);
		final Optional<List<T>> dataOp;
		if (fileData.isPresent()) {
			dataOp = fileData;
			log.info("[{}@{}] fetched from file success, in {}s", this.zhName(), date, ChronoUnit.SECONDS.between(fetchFileStartTime, LocalDateTime.now()));
		} else if (fetchFromApi) {
			final LocalDateTime fetchApiStartTime = LocalDateTime.now();
			dataOp = Optional.of(this.fetch(date)).filter(d -> !d.isEmpty());
			dataOp.ifPresent(Unchecked.consumer(data -> this.saveToFile(date, data)));
			dataOp.orElseThrow(() -> new MsgException(MsgTemplate.RMAPI02.build("本日無資料 {0}:{1}", this.zhName(), date)));
			log.info("[{}@{}] fetched from api success, in {}s", this.zhName(), date, ChronoUnit.SECONDS.between(fetchApiStartTime, LocalDateTime.now()));
		} else {
			dataOp = Optional.empty();
		}

		if (dao().countBySyncDate(date) <= 0) {
			dataOp.ifPresent(data -> this.saveToDb(date, data));
		}
	}

	/** 儲存到檔案 */
	private void saveToFile(LocalDate date, List<T> datas) throws IOException {
		final LocalDateTime saveFileStartTime = LocalDateTime.now();
		final Path path = Paths.get("data").resolve(YearMonth.from(date).toString()).resolve(date.toString());
		log.debug("[{}@{}] saving to file, {}", this.zhName(), date, path);

		// 沒有資料夾的話先建立
		if (!Files.isDirectory(path)) {
			Files.createDirectories(path);
			log.debug("[{}@{}] saving to file, created new folder, {}", this.zhName(), date, path);
		}

		// 若檔案已存在就刪除重寫
		final Path saveTo = path.resolve(fileName());
		if (Files.exists(saveTo)) {
			Files.delete(saveTo);
			log.debug("[{}@{}] saving to file, removed old data, {}", this.zhName(), date, path);
		}

		GlobalConstants.WEB_OBJECT_MAPPER.writeValue(saveTo.toFile(), datas);
		log.info("[{}@{}] saved to file success, in {}s, {}", this.zhName(), date, ChronoUnit.SECONDS.between(saveFileStartTime, LocalDateTime.now()), saveTo);
	}

	/**
	 * 從檔案讀取
	 * @return 若檔案不存在，則回傳為Empty
	 */
	private Optional<List<T>> loadFromFile(LocalDate date) throws IOException {
		final Path path = Paths.get("data").resolve(YearMonth.from(date).toString()).resolve(date.toString());
		final Path loadFrom = path.resolve(fileName());
		log.debug("[{}@{}] loaded from file, {}", this.zhName(), date, loadFrom);

		if (Files.exists(loadFrom)) {
			log.info("[{}@{}] loaded from file success", this.zhName(), date);
			return Optional.of(GlobalConstants.WEB_OBJECT_MAPPER.readValue(loadFrom.toFile(),
					GlobalConstants.WEB_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, entityClass())));
		} else {
			log.info("[{}@{}] loaded from file, but no file found, retrun empty, {}", this.zhName(), date, loadFrom);
			return Optional.empty();
		}
	}

	/** 儲存到 db */
	private void saveToDb(LocalDate date, List<T> datas) {
		final LocalDateTime saveDbStartTime = LocalDateTime.now();

		log.debug("[{}@{}] saving to db", this.zhName(), date);
		datas.forEach(e -> e.setSyncDate(date));
		// 根據日期全刪全增
		dao().deleteBySyncDate(date);
		try {
			this.batchSave(datas);
		} catch (NotImplementedException ignore) {
			dao().saveAll(datas);
		}

		log.info("[{}@{}] saved to db, in {}s", this.zhName(), date, ChronoUnit.SECONDS.between(saveDbStartTime, LocalDateTime.now()));
	}

	/** 批次儲存(透過JdbcTemplate) */
	private int batchSave(List<T> datas) {
		AtomicInteger result = new AtomicInteger();
		String sql = SqlUtils.batchInsert(entityClass());
		Iterables.partition(datas, 1000).forEach(partition -> {
			MapSqlParameterSource[] batchValuesMap = SqlUtils.getBatchValuesToMap(partition, data -> {});
			result.addAndGet(IntStream.of(this.jdbcTemplate.batchUpdate(sql, batchValuesMap)).sum());
		});

		return result.get();
	}

}
