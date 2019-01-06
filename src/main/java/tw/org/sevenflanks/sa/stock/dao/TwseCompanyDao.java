package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TwseCompanyDao extends SyncDateDao<TwseCompany> {

	@Query(value = "SELECT MAX(syncDate) FROM TwseCompany")
	LocalDate findLastSyncDate();

	@Query(value = "SELECT c FROM TwseCompany c WHERE c.syncDate = (SELECT MAX(sc.syncDate) FROM TwseCompany sc)")
	List<TwseCompany> findByLastSyncDate();

}
