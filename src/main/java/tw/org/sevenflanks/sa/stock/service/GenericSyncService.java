package tw.org.sevenflanks.sa.stock.service;

import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.config.GlobalConstants;
import tw.org.sevenflanks.sa.stock.dao.SyncDateDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;
import tw.org.sevenflanks.sa.stock.enums.DataStoreType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface GenericSyncService<T extends SyncDateEntity, DAO extends SyncDateDao<T>> {
	Logger log = LoggerFactory.getLogger(GenericSyncService.class);

	DAO dao();

	Class<T> entityClass();

	/** 從遠端抓資料 */
	List<T> fetch(LocalDate date);

	/** 檔案名稱 */
	String fileName();

	/** 中文名稱 */
	String zhName();

	default DataStoreType check(LocalDate date) {
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

	default void sync(LocalDate date, boolean fetchFromApi) throws IOException {
		// 先從檔案資料讀取，沒有的話再從Api撈
		final Optional<List<T>> fileData = this.loadFromFile(date);
		final Optional<List<T>> dataOp;
		if (fileData.isPresent()) {
			dataOp = fileData;
			log.info("[{}@{}] fetched dataOp success", this.zhName(), date);
		} else if (fetchFromApi) {
			dataOp = Optional.of(this.fetch(date)).filter(d -> !d.isEmpty());
			dataOp.ifPresent(Unchecked.consumer(data -> this.saveToFile(date, data)));
			dataOp.orElseThrow(() -> new MsgException(MsgTemplate.RMAPI02.build("本日無資料 {0}:{1}", this.zhName(), date)));
		} else {
			dataOp = Optional.empty();
		}

		if (dao().countBySyncDate(date) <= 0) {
			dataOp.ifPresent(data -> this.saveToDb(date, data));
		}
	}

	/** 儲存到檔案 */
	default void saveToFile(LocalDate date, List<T> datas) throws IOException {
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
		log.info("[{}@{}] saved to file success, {}", this.zhName(), date, saveTo);
	}

	/**
	 * 從檔案讀取
	 * @return 若檔案不存在，則回傳為Empty
	 */
	default Optional<List<T>> loadFromFile(LocalDate date) throws IOException {
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
	default List<T> saveToDb(LocalDate date, List<T> datas) {
		log.info("[{}@{}] saving to db", this.zhName(), date);
		datas.forEach(e -> e.setSyncDate(date));
		// 根據日期全刪全增
		dao().deleteBySyncDate(date);
		return dao().saveAll(datas);
	}

}
