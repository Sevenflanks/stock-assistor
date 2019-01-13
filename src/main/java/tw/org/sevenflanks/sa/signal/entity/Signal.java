package tw.org.sevenflanks.sa.signal.entity;

import lombok.*;
import tw.org.sevenflanks.sa.base.data.GenericEntity;
import tw.org.sevenflanks.sa.base.data.JsonModel;
import tw.org.sevenflanks.sa.signal.rule.SignalRule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Signal extends GenericEntity {

	/** 訊號代碼 */
	@Column(unique = true)
	private String code;

	/** 訊號名稱 */
	@Column
	private String name;

	/** 訊號短名稱 */
	@Column
	private String shortName;

	/** 使用的rule代碼 */
	@Column
	private String ruleCode;

	/** 因子 */
	@Column
	@Lob
	private JsonModel<?> factor;

	public <FACTOR> FACTOR readFactor() {
		if (factor != null) {
			return (FACTOR) factor.get();
		} else {
			return null;
		}
	}

	public Signal(String code, String name, String shortName, SignalRule rule, SignalRule.Factor factor) {
		this.code = code;
		this.name = name;
		this.shortName = shortName;
		this.ruleCode = rule.code();
		this.factor = JsonModel.builder().value(factor).build();
	}

}
