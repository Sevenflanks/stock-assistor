package tw.org.sevenflanks.sa.base.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tw.org.sevenflanks.sa.base.utils.JsonUtils;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonListModel<T> {

	private String json;

	private List<T> value;

	public List<T> get() {
		if (value == null) {
			try {
				this.value = JsonUtils.readListFormDb(json);
			} catch (Exception e) {
				e.printStackTrace();
				this.value = null;
			}
		}
		return value;
	}

	public JsonListModel(String json) {
		this.json = json;
	}
}
