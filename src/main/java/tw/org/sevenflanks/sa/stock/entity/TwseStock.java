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
import tw.org.sevenflanks.sa.stock.model.TwseDailyDetailModel;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
		@Index(name = "TWSESTOCK_IDX1", columnList = "syncDate"),
		@Index(name = "TWSESTOCK_IDX2", columnList = "uid")
})
public class TwseStock extends SyncDateEntity {

	/** 證券代號 */
	@Column
	private String uid;

	/** 證券名稱 */
	@Column
	private String fullName;

	/** 成交股數 */
	@Column
	private BigDecimal sharesTraded;

	/** 成交筆數 */
	@Column
	private BigDecimal transactions;

	/** 成交金額 */
	@Column
	private BigDecimal turnover;

	/** 開盤價 */
	@Column
	private BigDecimal openingPrice;

	/** 最高價 */
	@Column
	private BigDecimal highestPrice;

	/** 最低價 */
	@Column
	private BigDecimal lowestPrice;

	/** 收盤價 */
	@Column
	private BigDecimal closingPrice;

	/** 漲跌(+/-) */
	@Column
	private String upsDowns;

	/** 漲跌價差 */
	@Column
	private BigDecimal upsDownsSpread;

	/** 最後揭示買價 */
	@Column
	private BigDecimal fianllyPurchasePrice;

	/** 最後揭示買量 */
	@Column
	private BigDecimal finallyPurchase;

	/** 最後揭示賣價 */
	@Column
	private BigDecimal finallySellingPrice;

	/** 最後揭示賣量 */
	@Column
	private BigDecimal finallySales;

	/** 本益比 */
	@Column
	private BigDecimal peRatio;

	public TwseStock(TwseDailyDetailModel model) {
		BeanUtils.copyProperties(model, this);
	}
}
