package tw.org.sevenflanks.sa.base.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericDao<ENTITY extends GenericEntity> extends JpaRepository<ENTITY, Long> {

}
