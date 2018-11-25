package tw.org.sevenflanks.sa.stock.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;

import tw.org.sevenflanks.sa.base.data.GenericDao;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;

@NoRepositoryBean
public interface SyncDateDao<ENTITY extends SyncDateEntity> extends GenericDao<ENTITY> {

	List<ENTITY> findBySyncDate(LocalDate syncDate);

	int deleteBySyncDate(LocalDate syncDate);

}
