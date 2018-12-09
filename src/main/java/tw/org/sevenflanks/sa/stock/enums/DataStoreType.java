package tw.org.sevenflanks.sa.stock.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

public enum DataStoreType {

	FAILED(-1),

	NONE(0),

	FILE(1),

	DB(2);

	// 儲存等級，等級越高越完整
	@Getter
	private final int level;

	public DataStoreType from(int level) {
		return MAPPING.get(level);
	}

	private final static Map<Integer, DataStoreType> MAPPING = Stream.of(values())
			.collect(Collectors.toMap(DataStoreType::getLevel, Function.identity(), (o1, o2) -> o1));
	DataStoreType(int level) {
		this.level = level;
	}

}
