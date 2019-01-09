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

	public Signal(SignalRule rule, SignalRule.Factor factor) {
		this.ruleCode = rule.code();
		this.factor = JsonModel.builder().value(factor).build();
	}

}
