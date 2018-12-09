package tw.org.sevenflanks.sa.stock.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.org.sevenflanks.sa.stock.enums.DataStoreType;

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

	public static DataStoringModel error(LocalDate date) {
		return DataStoringModel.builder()
				.dataDate(date)
				.totalType(DataStoreType.FAILED)
				.build();
	}

	public DataStoreType getTotalType() {
		return Stream.of(otcCompany, otcRgremain, otcStock, twseCompany, twseRgremain, twseStock)
				.map(t -> Optional.ofNullable(t).orElse(DataStoreType.FAILED))
				.min(Comparator.comparing(DataStoreType::getLevel))
				.orElse(DataStoreType.NONE);
	}

}
