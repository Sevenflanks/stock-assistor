package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.stock.entity.OtcStock;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OtcStockDao extends SyncDateDao<OtcStock> {

	OtcStock findByUidAndSyncDate(String uid, LocalDate syncDate);

	@Query(value = "SELECT DISTINCT SYNC_DATE FROM OTC_STOCK WHERE UID = :uid AND SYNC_DATE <= :from ORDER BY SYNC_DATE DESC LIMIT :top", nativeQuery = true)
	List<Date> findRecordDates(String uid, LocalDate from, int top);

	@Query(value = "SELECT AVG(SHARES_TRADED) FROM OTC_STOCK WHERE UID = :uid AND SYNC_DATE IN (SELECT DISTINCT SYNC_DATE FROM OTC_STOCK WHERE UID = :uid AND SYNC_DATE < :from ORDER BY SYNC_DATE DESC LIMIT :top)", nativeQuery = true)
	BigDecimal findAvgSharesTraded(String uid, LocalDate from, int top);

}
