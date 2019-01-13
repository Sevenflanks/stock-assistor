package tw.org.sevenflanks.sa.signal.dao;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import tw.org.sevenflanks.sa.base.data.JsonListModel;
import tw.org.sevenflanks.sa.base.data.JsonModel;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;
import tw.org.sevenflanks.sa.signal.model.SignalVo;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("postgres")
@Transactional
@Rollback
public class SignalResultDaoTest {

    @Autowired
    private SignalResultDao signalResultDao;

    @Test
    public void test() {

        final CompanyVo companyVo = new CompanyVo();
        companyVo.setUid("1234");
        companyVo.setFullName("測試");
        companyVo.setStockType("測");
        final SignalVo signalVo = new SignalVo();
        signalVo.setCode("AAA");
        signalVo.setName("測試");
        signalVo.setShortName("測");
        final SignalResult signalResult = new SignalResult(
                JsonModel.<CompanyVo>builder().value(companyVo).build(),
                JsonListModel.<SignalVo>builder().value(Lists.newArrayList(signalVo)).build());

        final SignalResult saved = signalResultDao.save(signalResult);
        signalResultDao.findById(saved.getId());

    }

}
