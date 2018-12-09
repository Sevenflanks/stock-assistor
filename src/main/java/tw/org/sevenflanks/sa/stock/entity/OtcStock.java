package tw.org.sevenflanks.sa.stock.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.model.OtcStockDetailModel;

/** 上櫃股票行情 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(indexes = {
		@Index(name = "OTCSTOCK_IDX1", columnList = "syncDate"),
		@Index(name = "OTCSTOCK_IDX2", columnList = "uid")
})
public class OtcStock extends SyncDateEntity {

	/** 代號 */
	private String uid;

	/** 名稱 */
	private String fullName;

	/** 收盤 */
	private String closingPrice;

	/** 漲跌 */
	private String upsDowns;

	/** 開盤 */
	private BigDecimal openingPrice;

	/** 最高 */
	private BigDecimal highestPrice;

	/** 最低 */
	private BigDecimal lowestPrice;

	/** 均價 */
	private BigDecimal avgPrice;

	/** 成交股數 */
	private BigDecimal sharesTraded;

	/** 成交金額(元) */
	private BigDecimal turnover;

	/** 成交筆數 */
	private BigDecimal transactions;

	/** 最後買價 */
	private BigDecimal fianllyPurchasePrice;

	/** 最後賣價 */
	private BigDecimal finallySellingPrice;

	/** 發行股數 */
	private BigDecimal sharesTotal;

	/** 次日參考價 */
	private BigDecimal nextSuggestPrice;

	/** 次日漲停價 */
	private BigDecimal nextUpLimitPrice;

	/** 次日跌停價 */
	private BigDecimal nextDownLimitPrice;

	public OtcStock(OtcStockDetailModel model) {
		BeanUtils.copyProperties(model, this);
	}
}
