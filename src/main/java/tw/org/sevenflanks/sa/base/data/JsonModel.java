package tw.org.sevenflanks.sa.base.data;

import lombok.Builder;

@Builder
public class JsonModel<T> {

	private T value;

	public T get() {
		return value;
	}

}
