package tw.org.sevenflanks.sa.stock.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "uid")
public class CompanyVo {

	public static final String TYPE_OTC = "上櫃";
	public static final String TYPE_TWSE = "上市";

	/** 公司代號 */
	private String uid;

	/** 公司名稱 */
	private String fullName;

	/** 股票類型 */
	private String stockType;

	public CompanyVo(OtcCompany otcCompany) {
		this.uid = otcCompany.getUid();
		this.fullName = otcCompany.getFullName();
		this.stockType = TYPE_OTC;
	}

	public CompanyVo(TwseCompany twseCompany) {
		this.uid = twseCompany.getUid();
		this.fullName = twseCompany.getFullName();
		this.stockType = TYPE_TWSE;
	}


	@Override
	public String toString() {
		return "(" + stockType + ")[" + uid + "]" + fullName;
	}
}
