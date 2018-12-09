package tw.org.sevenflanks.sa.stock.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtcStockModel {

	/** 資料日期 */
	private String reportDate;

	/** 資料標題 */
	private String reportTitle;

	/** 總筆數 */
	private int iTotalRecords;

	/** 總顯示筆數 */
	private int iTotalDisplayRecords;

	/** 上櫃家數 */
	private String listNum;

	/** 總成交金額 */
	private String totalAmount;

	/** 總成交股數 */
	private String totalVolumn;

	/** 總成交筆數 */
	private String totalCount;

	/** 股票行情 */
	private List<OtcStockDetailModel> aaData;

	/** 管理股票行情 */
	private List<OtcStockDetailModel> mmData;

}
