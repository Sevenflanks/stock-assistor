package tw.org.sevenflanks.sa.stock.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import tw.org.sevenflanks.sa.config.GlobalConstants;
import tw.org.sevenflanks.sa.stock.dao.SyncDateDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;

public interface GenericSyncService<T extends SyncDateEntity, DAO extends SyncDateDao<T>> {

	DAO dao();

	/** 從遠端抓資料 */
	List<T> fetch(LocalDate date);

	/** 檔案名稱 */
	String fileName();

	/** 儲存到檔案 */
	default void saveToFile(LocalDate date, List<T> datas) throws IOException {
		final Path path = Paths.get("data").resolve(date.toString());

		// 沒有資料夾的話先建立
		if (!Files.isDirectory(path)) {
			Files.createDirectory(path);
		}

		// 若檔案已存在就刪除重寫
		final Path saveTo = path.resolve(fileName());
		if (Files.exists(saveTo)) {
			Files.delete(saveTo);
		}

		GlobalConstants.WEB_OBJECT_MAPPER.writeValue(saveTo.toFile(), datas);
	}

	/**
	 * 從檔案讀取
	 * @return 若檔案不存在，則回傳為Empty
	 */
	default Optional<List<T>> loadFromFile(LocalDate date) throws IOException {
		final Path path = Paths.get("data").resolve(date.toString());
		final Path loadFrom = path.resolve(fileName());
		if (Files.exists(loadFrom)) {
			return GlobalConstants.WEB_OBJECT_MAPPER.readValue(loadFrom.toFile(), new TypeReference<List<T>>(){});
		} else {
			return Optional.empty();
		}
	}

	/** 儲存到 db */
	default List<T> saveToDb(LocalDate date, List<T> datas) {
		datas.forEach(e -> e.setSyncDate(date));
		// 根據日期全刪全增
		dao().deleteBySyncDate(date);
		return dao().saveAll(datas);
	}

}
