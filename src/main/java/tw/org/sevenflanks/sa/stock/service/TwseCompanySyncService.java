package tw.org.sevenflanks.sa.stock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.picker.TwseCompanyPicker;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class TwseCompanySyncService extends AbstractSyncService<TwseCompany, TwseCompanyDao> {

    @Autowired
    private TwseCompanyDao twseCompanyDao;

    @Autowired
    private TwseCompanyPicker twseCompanyPicker;

    @Override
    public TwseCompanyDao dao() {
        return twseCompanyDao;
    }

    @Override
    public Class<TwseCompany> entityClass() {
        return TwseCompany.class;
    }

    public void syncOnlyLatest(LocalDate date, boolean fetchFromApi) throws IOException {
        LocalDate lastSyncDate = twseCompanyDao.findLastSyncDate();
        // 公司資料取最新就好，如果目標日期小於最後有資料的日期就不用再跑一次同步了
        if (lastSyncDate != null && lastSyncDate.isAfter(date)) {
            log.info("[{}@{}] already have newer data:{}, skip sync", this.zhName(), date, lastSyncDate);
        } else {
            super.sync(date, fetchFromApi);
        }
    }

    @Override
    public List<TwseCompany> fetch(LocalDate date) {
        // 公司別的API沒有日期輸入參數
        return Optional.ofNullable(twseCompanyPicker.getAll())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(TwseCompany::new)
                .collect(Collectors.toList());
    }

    @Override
    public String fileName() {
        return "twse_company";
    }

    @Override
    public String zhName() {
        return "上市公司資訊";
    }
}
