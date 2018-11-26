package tw.org.sevenflanks.sa.base.msg.model;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MsgBody<T> {

	private T data;
	private Msg msg;

	public static <T> ResponseEntity<MsgBody<T>> ok(T data) {
		return ResponseEntity.ok(new MsgBody<T>(data, MsgTemplate.MSG0000.build("操作成功")));
	}

	public static <T> ResponseEntity<MsgBody<T>> error(Throwable t) {
		return ResponseEntity.ok(new MsgBody<T>(null, MsgTemplate.API9999.build(Optional.ofNullable(t).map(Throwable::getMessage).orElse(null))));
	}

}
