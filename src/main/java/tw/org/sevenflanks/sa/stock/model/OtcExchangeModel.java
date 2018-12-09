package tw.org.sevenflanks.sa.stock.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtcExchangeModel {

	/** 資料日期 */
	private String reportDate;

	/** 總筆數 */
	private int iTotalRecords;

	/** 資料 */
	private List<OtcExchangeDetailModel> aaData;

}
