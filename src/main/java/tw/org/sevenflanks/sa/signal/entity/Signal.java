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
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Signal extends GenericEntity {

	@Column(unique = true)
	private String code;

	@Column
	private String name;

	@Column
	private String shortName;

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

	public Signal(String code, String name, String shortName, SignalRule rule, SignalRule.Factor factor) {
		this.code = code;
		this.name = name;
		this.shortName = shortName;
		this.ruleCode = rule.code();
		this.factor = JsonModel.builder().value(factor).build();
	}

}
