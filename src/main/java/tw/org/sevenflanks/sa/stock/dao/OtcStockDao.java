package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.stereotype.Repository;

import tw.org.sevenflanks.sa.stock.entity.OtcStock;

@Repository
public interface OtcStockDao extends SyncDateDao<OtcStock> {
}
