package tw.org.sevenflanks.sa.stock.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtcStockDetailModel extends ArrayList<String> {

	@Override
	public boolean add(String s) {
		// 櫃買中心提供的API，每筆資料是以Array描述，這邊利用jackson會呼叫add的方式轉成物件
		try {
			final Field[] fields = OtcStockDetailModel.class.getDeclaredFields();
			final int currIdx = this.size();
			final Field field = fields[currIdx];
			if (s == null || s.length() == 0 || s.contains("---")) {
				field.set(this, null);
			} else if (field.getType().isAssignableFrom(String.class)) {
				field.set(this, s);
			} else if (field.getType().isAssignableFrom(BigDecimal.class)) {
				field.set(this, new BigDecimal(s.replaceAll(",", "")));
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return super.add(s);
	}

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

	@Override
	public String toString() {
		return "OtcStockDetailModel{" +
				"uid='" + uid + '\'' +
				", fullName='" + fullName + '\'' +
				", closingPrice='" + closingPrice + '\'' +
				", upsDowns='" + upsDowns + '\'' +
				", openingPrice='" + openingPrice + '\'' +
				", highestPrice='" + highestPrice + '\'' +
				", lowestPrice='" + lowestPrice + '\'' +
				", avgPrice='" + avgPrice + '\'' +
				", sharesTraded='" + sharesTraded + '\'' +
				", turnover='" + turnover + '\'' +
				", transactions='" + transactions + '\'' +
				", fianllyPurchasePrice='" + fianllyPurchasePrice + '\'' +
				", finallySellingPrice='" + finallySellingPrice + '\'' +
				", sharesTotal='" + sharesTotal + '\'' +
				", nextSuggestPrice='" + nextSuggestPrice + '\'' +
				", nextUpLimitPrice='" + nextUpLimitPrice + '\'' +
				", nextDownLimitPrice='" + nextDownLimitPrice + '\'' +
				'}';
	}
}
