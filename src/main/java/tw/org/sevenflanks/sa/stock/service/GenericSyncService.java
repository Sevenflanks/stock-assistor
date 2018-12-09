package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.org.sevenflanks.sa.config.GlobalConstants;
import tw.org.sevenflanks.sa.stock.dao.SyncDateDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;

public interface GenericSyncService<T extends SyncDateEntity, DAO extends SyncDateDao<T>> {
	final Logger log = LoggerFactory.getLogger(GenericSyncService.class);

	DAO dao();

	Class<T> entityClass();

	/** 從遠端抓資料 */
	List<T> fetch(LocalDate date);

	/** 檔案名稱 */
	String fileName();

	default void syncToFileAndDb(LocalDate date) throws IOException {
		// 先從檔案資料讀取，沒有的話再從Api撈
		final Optional<List<T>> fileData = this.loadFromFile(date);
		final List<T> data;
		if (fileData.isPresent()) {
			data = fileData.get();
			log.info("[{}@{}] fetched data success", this.getClass().getSimpleName(), date);
		} else {
			data = this.fetch(date);
			this.saveToFile(date, data);
		}
		this.saveToDb(date, data);
	}

	/** 儲存到檔案 */
	default void saveToFile(LocalDate date, List<T> datas) throws IOException {
		final Path path = Paths.get("data").resolve(date.toString());
		log.debug("[{}@{}] saving to file, {}", this.getClass().getSimpleName(), date, path);

		// 沒有資料夾的話先建立
		if (!Files.isDirectory(path)) {
			Files.createDirectory(path);
			log.debug("[{}@{}] saving to file, created new folder, {}", this.getClass().getSimpleName(), date, path);
		}

		// 若檔案已存在就刪除重寫
		final Path saveTo = path.resolve(fileName());
		if (Files.exists(saveTo)) {
			Files.delete(saveTo);
			log.debug("[{}@{}] saving to file, removed old data, {}", this.getClass().getSimpleName(), date, path);
		}

		GlobalConstants.WEB_OBJECT_MAPPER.writeValue(saveTo.toFile(), datas);
		log.info("[{}@{}] saved to file success, {}", this.getClass().getSimpleName(), date, saveTo);
	}

	/**
	 * 從檔案讀取
	 * @return 若檔案不存在，則回傳為Empty
	 */
	default Optional<List<T>> loadFromFile(LocalDate date) throws IOException {
		final Path path = Paths.get("data").resolve(date.toString());
		final Path loadFrom = path.resolve(fileName());
		log.debug("[{}@{}] loaded from file, {}", this.getClass().getSimpleName(), date, loadFrom);

		if (Files.exists(loadFrom)) {
			log.info("[{}@{}] loaded from file success", this.getClass().getSimpleName(), date);
			return Optional.of(GlobalConstants.WEB_OBJECT_MAPPER.readValue(loadFrom.toFile(),
					GlobalConstants.WEB_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, entityClass())));
		} else {
			log.info("[{}@{}] loaded from file, but no file found, retrun empty, {}", this.getClass().getSimpleName(), date, loadFrom);
			return Optional.empty();
		}
	}

	/** 儲存到 db */
	default List<T> saveToDb(LocalDate date, List<T> datas) {
		log.info("[{}@{}] saving to db", this.getClass().getSimpleName(), date);
		datas.forEach(e -> e.setSyncDate(date));
		// 根據日期全刪全增
		dao().deleteBySyncDate(date);
		return dao().saveAll(datas);
	}

}
