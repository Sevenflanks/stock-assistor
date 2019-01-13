package tw.org.sevenflanks.sa.signal.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.base.data.JsonListModel;
import tw.org.sevenflanks.sa.base.data.JsonModel;
import tw.org.sevenflanks.sa.signal.model.SignalVo;
import tw.org.sevenflanks.sa.stock.entity.SyncDateEntity;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SignalResult extends SyncDateEntity {

	@Column
	@Lob
	private JsonModel<CompanyVo> company;

	@Column
	@Lob
	private JsonListModel<SignalVo> matchs;

}
