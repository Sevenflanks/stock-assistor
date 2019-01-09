package tw.org.sevenflanks.sa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import tw.org.sevenflanks.sa.base.data.JsonModel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Slf4j
@Converter(autoApply = true)
public class JsonModelConverter implements AttributeConverter<JsonModel<?>, String> {

	private final static ObjectMapper OM =
			Jackson2ObjectMapperBuilder.json().failOnUnknownProperties(false).build();

	private final static String SPLITER = "::";

	@Override
	public String convertToDatabaseColumn(JsonModel<?> jsonModel) {
		try {
			if (jsonModel != null) {
				final Object value = jsonModel.get();
				final String className = value.getClass().getName();
				final String json = OM.writeValueAsString(value);

				return className + SPLITER + json;
			}
		} catch (Exception e) {
			log.error("parse JsonModel to String failed", e);
		}
		return null;
	}

	@Override
	public JsonModel<?> convertToEntityAttribute(String s) {
		try {
			if (s != null) {
				final String[] splited = s.split(SPLITER);
				final Class<?> valueClass = Class.forName(splited[0]);
				final Object value = OM.readValue(splited[1], valueClass);
				return JsonModel.builder().value(value).build();
			}
		} catch (Exception e) {
			log.error("parse JsonModel to Object failed", e);
		}

		return null;
	}
}
