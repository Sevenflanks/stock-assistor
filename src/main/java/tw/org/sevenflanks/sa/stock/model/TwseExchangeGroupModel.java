package tw.org.sevenflanks.sa.stock.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TwseExchangeGroupModel {

	private int start;
	private int span;
	private String title;

}
