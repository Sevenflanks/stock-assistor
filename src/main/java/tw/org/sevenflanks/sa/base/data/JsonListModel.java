package tw.org.sevenflanks.sa.base.data;

import lombok.Builder;

import java.util.List;

@Builder
public class JsonListModel<T> {

	private List<T> value;

	public List<T> get() {
		return value;
	}

}
