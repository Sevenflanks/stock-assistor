package tw.org.sevenflanks.sa.stock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tw.org.sevenflanks.sa.stock.model.TwseCompanyModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/** 上市公司資料 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
		@Index(name = "TWSECOMPANY_IDX1", columnList = "syncDate"),
		@Index(name = "TWSECOMPANY_IDX2", columnList = "uid")
})
public class TwseCompany extends SyncDateEntity implements Company {

	/** 出表日期 */
	@Column
	private String dataDate;

	/** 公司代號 */
	@Column
	private String uid;

	/** 公司名稱 */
	@Column
	private String fullName;

	/** 公司簡稱 */
	@Column
	private String shortName;

	/** 外國企業註冊地國 */
	@Column
	private String country;

	/** 產業別 */
	@Column
	private String industry;

	/** 住址 */
	@Column
	private String address;

	/** 營利事業統一編號 */
	@Column
	private String taxId;

	/** 董事長 */
	@Column
	private String chairman;

	/** 總經理 */
	@Column
	private String gm;

	/** 發言人 */
	@Column
	private String spokesman;

	/** 發言人職稱 */
	@Column
	private String spokesmanTitie;

	/** 代理發言人 */
	@Column
	private String spokesmanAssist;

	/** 總機電話 */
	@Column
	private String phone;

	/** 成立日期 */
	@Column
	private String establishDate;

	/** 上市日期 */
	@Column
	private String listedDate;

	/** 普通股每股面額 */
	@Column
	private String ordinaryValue;

	/** 實收資本額 */
	@Column
	private String contributedCapital;

	/** 私募股數 */
	@Column
	private String other1;

	/** 特別股 */
	@Column
	private String other2;

	/** 編制財務報表類型 */
	@Column
	private String other3;

	/** 股票過戶機構 */
	@Column
	private String other4;

	/** 過戶電話 */
	@Column
	private String other5;

	/** 過戶地址 */
	@Column
	private String other6;

	/** 簽證會計師事務所 */
	@Column
	private String other7;

	/** 簽證會計師1 */
	@Column
	private String other8;

	/** 簽證會計師2 */
	@Column
	private String other9;

	/** 英文簡稱 */
	@Column
	private String shortEngName;

	/** 英文通訊地址 */
	@Column
	private String engAddress;

	/** 傳真機號碼 */
	@Column
	private String fax;

	/** 電子郵件信箱 */
	@Column
	private String email;

	/** 網址 */
	@Column
	private String homepage;

	public TwseCompany(TwseCompanyModel model) {
		BeanUtils.copyProperties(model, this);
	}
}
