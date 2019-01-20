package tw.org.sevenflanks.sa.signal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.signal.entity.SignalResult;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignalResultVo {

    private CompanyVo company;
    private List<SignalVo> matched;

    public SignalResultVo(SignalResult entity) {
        company = entity.getCompany().get();
        matched = entity.getMatchs().get();
    }

}
