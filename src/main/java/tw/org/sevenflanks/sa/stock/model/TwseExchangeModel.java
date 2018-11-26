package tw.org.sevenflanks.sa.stock.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TwseExchangeModel {

	private List<TwseExchangeDetailModel> data;
	private String date;
	private List<String> fields;
	private List<TwseExchangeGroupModel> groups;
	private List<String> notes;
	private String stat;
	private String title;

}
