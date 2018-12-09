package tw.org.sevenflanks.sa.stock.dao;

import org.springframework.stereotype.Repository;

import tw.org.sevenflanks.sa.stock.entity.OtcCompany;

@Repository
public interface OtcCompanyDao extends SyncDateDao<OtcCompany> {
}
