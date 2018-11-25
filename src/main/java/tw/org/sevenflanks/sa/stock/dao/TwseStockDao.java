package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.stereotype.Repository;

import tw.org.sevenflanks.sa.stock.entity.TwseStock;

@Repository
public interface TwseStockDao extends SyncDateDao<TwseStock> {
}
