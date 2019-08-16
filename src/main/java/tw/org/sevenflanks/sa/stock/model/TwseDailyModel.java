package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TwseDailyModel {

	private List<String> fields9;
	private List<TwseDailyDetailModel> data9;

}
