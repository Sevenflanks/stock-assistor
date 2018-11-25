package tw.org.sevenflanks.sa.base.msg.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.base.msg.enums.MsgLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Msg {

	private MsgLevel level;
	private String code;
	private String title;
	private String desc;

	@Override
	public String toString() {
		return "(" + level + ")[" + code + ":" + title + "] " + desc;

	}
}
