package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TwseStockDao extends SyncDateDao<TwseStock> {

	TwseStock findByUidAndSyncDate(String uid, LocalDate syncDate);

	@Query(value = "SELECT DISTINCT SYNC_DATE FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE <= :from ORDER BY SYNC_DATE DESC LIMIT :top", nativeQuery = true)
	List<Date> findRecordDates(String uid, LocalDate from, int top);

	@Query(value = "SELECT AVG(SHARES_TRADED) FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE IN (SELECT DISTINCT SYNC_DATE FROM TWSE_STOCK WHERE UID = :uid AND SYNC_DATE < :from ORDER BY SYNC_DATE DESC LIMIT :top)", nativeQuery = true)
	BigDecimal findAvgSharesTraded(String uid, LocalDate from, int top);

}
