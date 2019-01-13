package tw.org.sevenflanks.sa.signal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.signal.entity.Signal;

@Getter
@Setter
@NoArgsConstructor
public class SignalVo {

    /** 訊號代碼 */
    private String code;

    /** 訊號名稱 */
    private String name;

    /** 訊號短名稱 */
    private String shortName;

    public SignalVo(Signal signal) {
        this.code = signal.getCode();
        this.name = signal.getName();
        this.shortName = signal.getShortName();
    }
}
