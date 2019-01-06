package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.data.repository.NoRepositoryBean;
import tw.org.sevenflanks.sa.base.data.GenericDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface SyncDateDao<ENTITY extends SyncDateEntity> extends GenericDao<ENTITY> {

	List<ENTITY> findBySyncDate(LocalDate syncDate);

	long countBySyncDate(LocalDate syncDate);

	int deleteBySyncDate(LocalDate syncDate);

}
