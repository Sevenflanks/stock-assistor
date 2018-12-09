package tw.org.sevenflanks.sa.base.msg.enums;

import static tw.org.sevenflanks.sa.base.msg.enums.MsgLevel.DANGER;
import static tw.org.sevenflanks.sa.base.msg.enums.MsgLevel.INFO;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.base.msg.model.Msg;

public enum MsgTemplate {
	MSG0000(INFO, "正常"),

	API0001(DANGER, "參數有誤"),
	API9999(DANGER, "發生預期外的錯誤"),

	RMAPI01(DANGER, "遠端API連線異常"),
	RMAPI02(DANGER, "遠端API提供資料為空"),
	RMAPI03(DANGER, "遠端API回應非成功代碼"),

	SYS9999(INFO, "系統異常"),
	;

	private final String title;
	private final MsgLevel level;

	public Msg build(String desc, Object... objs) {
		return new Msg(this.level, this.name(), this.title, MessageFormat.format(desc, objs));
	}

	MsgTemplate(MsgLevel level, String title) {
		this.title = title;
		this.level = level;
	}

	public static <T> T requireBody(String name, String url, ResponseEntity<T> response) {
		final HttpStatus statusCode = response.getStatusCode();
		final T body = response.getBody();

		if (statusCode.isError()) {
			throw new MsgException(MsgTemplate.RMAPI01.build(name + ":{0}, Response={1}", url, response));
		} else if (body == null) {
			throw new MsgException(MsgTemplate.RMAPI02.build(name + ":{0}, Response={1}", url, response));
		} else {
			return body;
		}
	}
}
