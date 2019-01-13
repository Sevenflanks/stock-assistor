package tw.org.sevenflanks.sa.signal.dao;

import org.springframework.stereotype.Repository;
import tw.org.sevenflanks.sa.base.data.GenericDao;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;

import java.time.LocalDate;

@Repository
public interface SignalResultDao extends GenericDao<SignalResult> {

    void deleteBySyncDate(LocalDate syncDate);

}
