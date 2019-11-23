package tw.org.sevenflanks.sa.stock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.stock.dao.OtcRgremainDao;
import tw.org.sevenflanks.sa.stock.entity.OtcRgremain;
import tw.org.sevenflanks.sa.stock.model.OtcExchangeModel;
import tw.org.sevenflanks.sa.stock.picker.OtcRgremainPicker;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class OtcRgremainSyncService extends AbstractSyncService<OtcRgremain, OtcRgremainDao> {

    @Autowired
    private OtcRgremainDao otcStockDao;

    @Autowired
    private OtcRgremainPicker otcRgremainPicker;

    @Override
    public OtcRgremainDao dao() {
        return otcStockDao;
    }

    @Override
    public Class<OtcRgremain> entityClass() {
        return OtcRgremain.class;
    }

    @Override
    public List<OtcRgremain> fetch(LocalDate date) {
        return Optional.ofNullable(otcRgremainPicker.getStockDay(date))
                .map(OtcExchangeModel::getAaData)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(OtcRgremain::new)
                .collect(Collectors.toList());
    }

    @Override
    public String fileName() {
        return "otc_rgremain";
    }

    @Override
    public String zhName() {
        return "上櫃融券餘額";
    }
}
