package tw.org.sevenflanks.sa.config;

import javax.persistence.AttributeConverter;
import java.sql.Date;
import java.time.LocalDate;

//@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

	@Override
	public Date convertToDatabaseColumn(LocalDate localDate) {
		return localDate != null ? Date.valueOf(localDate) : null;
	}

	@Override
	public LocalDate convertToEntityAttribute(Date sqlDate) {
		return sqlDate != null ? sqlDate.toLocalDate() : null;
	}
}
