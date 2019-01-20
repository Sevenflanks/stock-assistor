package tw.org.sevenflanks.sa.signal.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.base.data.GenericDao;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SignalResultDao extends GenericDao<SignalResult> {

    void deleteBySyncDate(LocalDate syncDate);

    @Query(value = "SELECT MAX(syncDate) FROM SignalResult")
    LocalDate findLastSyncDate();

    @Query(value = "SELECT c FROM SignalResult c WHERE c.syncDate = (SELECT MAX(sc.syncDate) FROM SignalResult sc) ORDER BY c.size desc, c.uid")
    List<SignalResult> findByLastSyncDate();

    @Query(value = "SELECT c FROM SignalResult c WHERE c.syncDate = (SELECT MAX(sc.syncDate) FROM SignalResult sc) ORDER BY c.size desc, c.uid")
    List<SignalResult> findByLastSyncDate(Pageable pageable);
}
