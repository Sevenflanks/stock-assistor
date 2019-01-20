package tw.org.sevenflanks.sa.base.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tw.org.sevenflanks.sa.base.utils.JsonUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonModel<T> {

	private String json;

	private T value;

	public T get() {
		if (value == null) {
			try {
				this.value = JsonUtils.readLFormDb(json);
			} catch (Exception e) {
				e.printStackTrace();
				this.value = null;
			}
		}
		return value;
	}

	public JsonModel(String json) {
		this.json = json;
	}
}
