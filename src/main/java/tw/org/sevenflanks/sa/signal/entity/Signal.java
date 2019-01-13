package tw.org.sevenflanks.sa.signal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.base.data.GenericEntity;
import tw.org.sevenflanks.sa.base.data.JsonModel;
import tw.org.sevenflanks.sa.signal.rule.SignalRule;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Signal extends GenericEntity {

	@Column
	private String code;

	@Column
	private String name;

	@Column
	private String ruleCode;

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

	public Signal(String code, String name, SignalRule rule, SignalRule.Factor factor) {
		this.code = code;
		this.name = name;
		this.ruleCode = rule.code();
		this.factor = JsonModel.builder().value(factor).build();
	}

}
