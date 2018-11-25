package tw.org.sevenflanks.sa.stock.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TwseDailyModel {

	private List<String> fields5;
	private List<TwseDailyDetailModel> data5;

}
