package tw.org.sevenflanks.sa.signal.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.base.data.GenericEntity;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Signal extends GenericEntity {

	private String ruleCode;

}
