package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
@Setter
public class TwseDailyDetailModel extends ArrayList<String> {

	@Override
	public boolean add(String s) {
		// 證交所提供的API，每筆資料是以Array描述，這邊利用jackson會呼叫add的方式轉成物件
		try {
			final Field[] fields = TwseDailyDetailModel.class.getDeclaredFields();
			final int currIdx = this.size();
			final Field field = fields[currIdx];
			if (s == null || "--".equals(s) || s.length() == 0) {
				field.set(this, null);
			} else if (field.getType().isAssignableFrom(String.class)) {
				field.set(this, s);
			} else if (field.getType().isAssignableFrom(BigDecimal.class)) {
				field.set(this, new BigDecimal(s.replaceAll(",", "")));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return super.add(s);
	}

	/** 證券代號 */
	private String uid;

	/** 證券名稱 */
	private String fullName;

	/** 成交股數 */
	private BigDecimal sharesTraded;

	/** 成交筆數 */
	private BigDecimal transactions;

	/** 成交金額 */
	private BigDecimal turnover;

	/** 開盤價 */
	private BigDecimal openingPrice;

	/** 最高價 */
	private BigDecimal highestPrice;

	/** 最低價 */
	private BigDecimal lowestPrice;

	/** 收盤價 */
	private BigDecimal closingPrice;

	/** 漲跌(+/-) */
	private String upsDowns;

	/** 漲跌價差 */
	private BigDecimal upsDownsSpread;

	/** 最後揭示買價 */
	private BigDecimal fianllyPurchasePrice;

	/** 最後揭示買量 */
	private BigDecimal finallyPurchase;

	/** 最後揭示賣價 */
	private BigDecimal finallySellingPrice;

	/** 最後揭示賣量 */
	private BigDecimal finallySales;

	/** 本益比 */
	private BigDecimal peRatio;

	@Override
	public String toString() {
		return "TwseDailyDetailModel{" +
				"uid='" + uid + '\'' +
				", fullName='" + fullName + '\'' +
				", sharesTraded=" + sharesTraded +
				", transactions=" + transactions +
				", turnover=" + turnover +
				", openingPrice=" + openingPrice +
				", highestPrice=" + highestPrice +
				", lowestPrice=" + lowestPrice +
				", closingPrice=" + closingPrice +
				", upsDowns='" + upsDowns + '\'' +
				", upsDownsSpread=" + upsDownsSpread +
				", fianllyPurchasePrice=" + fianllyPurchasePrice +
				", finallyPurchase=" + finallyPurchase +
				", finallySellingPrice=" + finallySellingPrice +
				", finallySales=" + finallySales +
				", peRatio=" + peRatio +
				'}';
	}
}
