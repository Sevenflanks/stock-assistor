package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.stereotype.Repository;

import tw.org.sevenflanks.sa.stock.entity.TwseCompany;

@Repository
public interface TwseCompanyDao extends SyncDateDao<TwseCompany> {
}
