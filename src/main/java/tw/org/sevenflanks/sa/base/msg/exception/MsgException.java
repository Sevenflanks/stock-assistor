package tw.org.sevenflanks.sa.base.msg.exception;

import lombok.Getter;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.model.Msg;

public class MsgException extends RuntimeException {

	@Getter
	private Msg msg;

	public MsgException(Msg msg) {
		super(msg.toString());
		this.msg = msg;
	}

	public MsgException(Msg msg, Throwable cause) {
		super(msg.toString(), cause);
		this.msg = msg;
	}

	public MsgException(Throwable cause) {
		super(cause);
		this.msg = MsgTemplate.SYS9999.build(cause.getMessage());
	}

	public MsgException(Msg msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(msg.toString(), cause, enableSuppression, writableStackTrace);
		this.msg = msg;
	}

}
