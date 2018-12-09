package tw.org.sevenflanks.sa.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;

@Configuration
public class MvcConfig {

	@Primary
	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
				.failOnEmptyBeans(false)
				.failOnUnknownProperties(false)
				.indentOutput(false)
				.modules(
						customJavaTimeModule()
				)
				.build();

		objectMapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false);
		GlobalConstants.WEB_OBJECT_MAPPER = objectMapper;
		return objectMapper;
	}

	private JavaTimeModule customJavaTimeModule() {
		final JavaTimeModule module = new JavaTimeModule();

		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(GlobalConstants.DATE_TIME_FORMATTER));
		module.addSerializer(new LocalDateTimeSerializer(GlobalConstants.DATE_TIME_FORMATTER));

		module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(GlobalConstants.TIME_FORMATTER));
		module.addSerializer(new LocalTimeSerializer(GlobalConstants.TIME_FORMATTER));

		module.addDeserializer(LocalDate.class, new LocalDateDeserializer(GlobalConstants.DATE_FORMATTER));
		module.addSerializer(new LocalDateSerializer(GlobalConstants.DATE_FORMATTER));

		module.addDeserializer(YearMonth.class, new YearMonthDeserializer(GlobalConstants.YEAR_MONTH_FORMATTER));
		module.addSerializer(new YearMonthSerializer(GlobalConstants.YEAR_MONTH_FORMATTER));

		return module;
	}
}
