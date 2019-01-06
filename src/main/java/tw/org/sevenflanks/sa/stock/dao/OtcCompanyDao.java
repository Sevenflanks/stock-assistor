package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OtcCompanyDao extends SyncDateDao<OtcCompany> {

	@Query(value = "SELECT MAX(syncDate) FROM OtcCompany")
	LocalDate findLastSyncDate();

	@Query(value = "SELECT c FROM OtcCompany c WHERE c.syncDate = (SELECT MAX(sc.syncDate) FROM OtcCompany sc)")
	List<OtcCompany> findByLastSyncDate();

}
