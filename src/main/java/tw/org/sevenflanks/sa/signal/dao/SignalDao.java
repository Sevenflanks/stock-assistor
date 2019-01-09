package tw.org.sevenflanks.sa.signal.dao;

import org.springframework.stereotype.Repository;

import tw.org.sevenflanks.sa.base.data.GenericDao;
import tw.org.sevenflanks.sa.signal.entity.Signal;

@Repository
public interface SignalDao extends GenericDao<Signal> {
}
