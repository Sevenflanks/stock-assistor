package tw.org.sevenflanks.sa.stock.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.model.TwseExchangeDetailModel;

/** 上市融券餘額 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
		@Index(name = "TWSERGREMAIN_IDX1", columnList = "syncDate"),
		@Index(name = "TWSERGREMAIN_IDX2", columnList = "uid")
})
public class TwseRgremain extends SyncDateEntity {

	/** 股票代號 */
	@Column
	private String uid;

	/** 股票名稱 */
	@Column
	private String fullName;

	/** 融券-前日餘額 */
	@Column
	private BigDecimal marginPreBalance;

	/** 融券-賣出 */
	@Column
	private BigDecimal marginSold;

	/** 融券-買進 */
	@Column
	private BigDecimal marginBought;

	/** 融券-現券 */
	@Column
	private BigDecimal marginCurrent;

	/** 融券-今日餘額 */
	@Column
	private BigDecimal marginBalance;

	/** 融券-限額 */
	@Column
	private BigDecimal marginLimit;

	/** 借券-前日餘額 */
	@Column
	private BigDecimal borrowingPreBalance;

	/** 借券-當日賣出 */
	@Column
	private BigDecimal borrowingSold;

	/** 借券-當日還券 */
	@Column
	private BigDecimal borrowingBought;

	/** 借券-當日調整 */
	@Column
	private BigDecimal borrowingAdjustment;

	/** 借券-當日餘額 */
	@Column
	private BigDecimal borrowingBalance;

	/** 借券-今日可限額 */
	@Column
	private BigDecimal borrowingLimit;

	/** 備註 */
	@Column
	private String remark;

	public TwseRgremain(TwseExchangeDetailModel model) {
		BeanUtils.copyProperties(model, this);
	}
}
