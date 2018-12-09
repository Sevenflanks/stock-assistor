package tw.org.sevenflanks.sa.stock.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtcExchangeDetailModel extends ArrayList<String> {

	@Override
	public boolean add(String s) {
		// 證交所提供的API，每筆資料是以Array描述，這邊利用jackson會呼叫add的方式轉成物件
		try {
			final Field[] fields = OtcExchangeDetailModel.class.getDeclaredFields();
			final int currIdx = this.size();
			final Field field = fields[currIdx];
			if (s == null || "--".equals(s) || s.length() == 0) {
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

	/** 股票代號 */
	private String uid;

	/** 股票名稱 */
	private String fullName;

	/** 融券-前日餘額 */
	private BigDecimal marginPreBalance;

	/** 融券-賣出 */
	private BigDecimal marginSold;

	/** 融券-買進 */
	private BigDecimal marginBought;

	/** 融券-現券 */
	private BigDecimal marginCurrent;

	/** 融券-今日餘額 */
	private BigDecimal marginBalance;

	/** 融券-限額 */
	private BigDecimal marginLimit;

	/** 借券-前日餘額 */
	private BigDecimal borrowingPreBalance;

	/** 借券-當日賣出 */
	private BigDecimal borrowingSold;

	/** 借券-當日還券 */
	private BigDecimal borrowingBought;

	/** 借券-當日調整 */
	private BigDecimal borrowingAdjustment;

	/** 借券-當日餘額 */
	private BigDecimal borrowingBalance;

	/** 借券-今日可限額 */
	private BigDecimal borrowingLimit;

	/** 備註 */
	private String remark;

	@Override
	public String toString() {
		return "TwseExchangeDetailModel{" +
				"uid='" + uid + '\'' +
				", fullName='" + fullName + '\'' +
				", marginPreBalance=" + marginPreBalance +
				", marginSold=" + marginSold +
				", marginBought=" + marginBought +
				", marginCurrent=" + marginCurrent +
				", marginBalance=" + marginBalance +
				", marginLimit=" + marginLimit +
				", borrowingPreBalance=" + borrowingPreBalance +
				", borrowingSold=" + borrowingSold +
				", borrowingBought=" + borrowingBought +
				", borrowingAdjustment=" + borrowingAdjustment +
				", borrowingBalance=" + borrowingBalance +
				", borrowingLimit=" + borrowingLimit +
				", remark='" + remark + '\'' +
				'}';
	}
}
