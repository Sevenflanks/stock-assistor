package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Transactional
public interface TwseStockDao extends SyncDateDao<TwseStock> {

	TwseStock findByUidAndSyncDate(String uid, LocalDate syncDate);

	@Query(value = "SELECT DISTINCT SYNC_DATE FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE <= :from ORDER BY SYNC_DATE DESC LIMIT :top", nativeQuery = true)
	Stream<Date> findRecordDatesNative(String uid, LocalDate from, int top);

	default List<LocalDate> findRecordDates(String uid, LocalDate from, int top) {
		// FIXME 因為某種不明原因 JPA沒辦法直接吐回List<LocalDate> 因此需要做一段轉手
		// FIXME 這點就算實作了 AttributeConverter<LocalDate, Date> 亦無法解決
		return findRecordDatesNative(uid, from, top).map(Date::toLocalDate).collect(Collectors.toList());
	}

	@Query(value = "SELECT AVG(SHARES_TRADED) FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE IN (SELECT DISTINCT SYNC_DATE FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE < :from ORDER BY SYNC_DATE DESC LIMIT :top)", nativeQuery = true)
	BigDecimal findAvgSharesTraded(String uid, LocalDate from, int top);

}
