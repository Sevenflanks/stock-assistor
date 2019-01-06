package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
public class CompanyVo {

	/** 公司代號 */
	@Column
	private String uid;

	/** 公司名稱 */
	@Column
	private String fullName;

	public CompanyVo(OtcCompany otcCompany) {
		this.uid = otcCompany.getUid();
		this.fullName = otcCompany.getFullName();
	}

	public CompanyVo(TwseCompany twseCompany) {
		this.uid = twseCompany.getUid();
		this.fullName = twseCompany.getFullName();
	}

	@Override
	public String toString() {
		return "CompanyVo{" +
				"uid='" + uid + '\'' +
				", fullName='" + fullName + '\'' +
				'}';
	}
}
