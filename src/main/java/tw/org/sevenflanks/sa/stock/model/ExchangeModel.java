package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ExchangeModel {

	private List<List<String>> data;
	private String date;
	private List<String> fields;
	private List<ExchangeGroupModel> groups;
	private List<String> notes;
	private String stat;
	private String title;

}
