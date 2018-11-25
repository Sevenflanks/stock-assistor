package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 上市公司
 * ps. 有些欄位感覺用不到而且感覺很難命名，先用other代替
 */
@Getter
@Setter
@NoArgsConstructor
public class TwseCompanyModel {

	/** 出表日期 */
	private String dataDate;

	/** 公司代號 */
	private String uid;

	/** 公司名稱 */
	private String fullName;

	/** 公司簡稱 */
	private String shortName;

	/** 外國企業註冊地國 */
	private String country;

	/** 產業別 */
	private String industry;

	/** 住址 */
	private String address;

	/** 營利事業統一編號 */
	private String taxId;

	/** 董事長 */
	private String chairman;

	/** 總經理 */
	private String gm;

	/** 發言人 */
	private String spokesman;

	/** 發言人職稱 */
	private String spokesmanTitie;

	/** 代理發言人 */
	private String spokesmanAssist;

	/** 總機電話 */
	private String phone;

	/** 成立日期 */
	private String establishDate;

	/** 上市日期 */
	private String listedDate;

	/** 普通股每股面額 */
	private String ordinaryValue;

	/** 實收資本額 */
	private String contributedCapital;

	/** 私募股數 */
	private String other1;

	/** 特別股 */
	private String other2;

	/** 編制財務報表類型 */
	private String other3;

	/** 股票過戶機構 */
	private String other4;

	/** 過戶電話 */
	private String other5;

	/** 過戶地址 */
	private String Other6;

	/** 簽證會計師事務所 */
	private String other7;

	/** 簽證會計師1 */
	private String other8;

	/** 簽證會計師2 */
	private String other9;

	/** 英文簡稱 */
	private String shortEngName;

	/** 英文通訊地址 */
	private String engAddress;

	/** 傳真機號碼 */
	private String fax;

	/** 電子郵件信箱 */
	private String email;

	/** 網址 */
	private String homepage;

	@Override
	public String toString() {
		return "TwseCompanyModel{" +
				"dataDate='" + dataDate + '\'' +
				", uid='" + uid + '\'' +
				", fullName='" + fullName + '\'' +
				", shortName='" + shortName + '\'' +
				", country='" + country + '\'' +
				", industry='" + industry + '\'' +
				", address='" + address + '\'' +
				", taxId='" + taxId + '\'' +
				", chairman='" + chairman + '\'' +
				", gm='" + gm + '\'' +
				", spokesman='" + spokesman + '\'' +
				", spokesmanTitie='" + spokesmanTitie + '\'' +
				", spokesmanAssist='" + spokesmanAssist + '\'' +
				", phone='" + phone + '\'' +
				", establishDate='" + establishDate + '\'' +
				", listedDate='" + listedDate + '\'' +
				", ordinaryValue='" + ordinaryValue + '\'' +
				", contributedCapital='" + contributedCapital + '\'' +
				", shortEngName='" + shortEngName + '\'' +
				", engAddress='" + engAddress + '\'' +
				", fax='" + fax + '\'' +
				", email='" + email + '\'' +
				", homepage='" + homepage + '\'' +
				'}';
	}
}
