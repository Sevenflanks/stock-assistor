package tw.org.sevenflanks.sa.base.msg.enums;

import lombok.Getter;

public enum MsgLevel {

	INFO(3), // 普通的訊息, 不仔細看也沒差
	IMPORTANT(2), // 重要的訊息, 不影響系統運行但是必須要人為處理
	DANGER(1) // 影響系統運行的訊息
	;

	@Getter
	private int value;

	MsgLevel(int value) {
		this.value = value;
	}

	public boolean isGE(MsgLevel that) {
		return this.value >= that.value;
	}

}
