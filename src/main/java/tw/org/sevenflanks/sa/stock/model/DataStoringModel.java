package tw.org.sevenflanks.sa.stock.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.enums.DataStoreType;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
public class DataStoringModel {

	private LocalDate dataDate;
	private DataStoreType otcCompany;
	private DataStoreType otcRgremain;
	private DataStoreType otcStock;
	private DataStoreType twseCompany;
	private DataStoreType twseRgremain;
	private DataStoreType twseStock;
	private DataStoreType totalType;
	private String msg;

	public static DataStoringModel error(LocalDate date, Throwable t) {
		return DataStoringModel.builder()
				.dataDate(date)
				.totalType(DataStoreType.FAILED)
				.msg(t.getMessage())
				.build();
	}

	/**
	 * 取得總計的類型.
	 * 由於公司資料不見得會每一日都有儲存，因此不檢查
	 */
	public DataStoreType getTotalType() {
		return Stream.of(otcRgremain, otcStock, twseRgremain, twseStock)
				.map(t -> Optional.ofNullable(t).orElse(DataStoreType.FAILED))
				.min(Comparator.comparing(DataStoreType::getLevel))
				.orElse(DataStoreType.NONE);
	}

}
