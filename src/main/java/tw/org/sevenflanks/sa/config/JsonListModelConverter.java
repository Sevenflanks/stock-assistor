package tw.org.sevenflanks.sa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import tw.org.sevenflanks.sa.base.data.JsonListModel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class JsonListModelConverter implements AttributeConverter<JsonListModel<?>, String> {

	private final static ObjectMapper OM =
			Jackson2ObjectMapperBuilder.json().failOnUnknownProperties(false).build();

	private final static String SPLITER = "::";

	@Override
	public String convertToDatabaseColumn(JsonListModel<?> jsonModel) {
		try {
			if (jsonModel != null) {
				final List<?> value = jsonModel.get();
				final String className = value.size() > 0 ? value.get(0).getClass().getName() : Object.class.getName();
				final String json = OM.writeValueAsString(value);
				return className + SPLITER + json;
			}
		} catch (Exception e) {
			log.error("parse JsonModel to String failed", e);
		}
		return null;
	}

	@Override
	public JsonListModel<?> convertToEntityAttribute(String s) {
		try {
			if (s != null) {
				final String[] splited = s.split(SPLITER);
				final Class<?> valueClass = Class.forName(splited[0]);
				final List<Object> value = OM.readValue(splited[1], OM.getTypeFactory().constructCollectionType(List.class, valueClass));
				return JsonListModel.builder().value(value).build();
			}
		} catch (Exception e) {
			log.error("parse JsonModel to Object failed", e);
		}

		return null;
	}

}
