package tw.org.sevenflanks.sa.config;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.base.data.JsonListModel;
import tw.org.sevenflanks.sa.base.utils.JsonUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Slf4j
@Converter(autoApply = true)
public class JsonListModelConverter implements AttributeConverter<JsonListModel<?>, String> {

	@Override
	public String convertToDatabaseColumn(JsonListModel<?> jsonModel) {
		try {
			if (jsonModel != null) {
				final List<?> value = jsonModel.get();
				final String className = value.size() > 0 ? value.get(0).getClass().getName() : Object.class.getName();
				final String json = JsonUtils.OM.writeValueAsString(value);
				return className + JsonUtils.SPLITER + json;
			}
		} catch (Exception e) {
			log.error("parse JsonModel to String failed", e);
		}
		return null;
	}

	@Override
	public JsonListModel<?> convertToEntityAttribute(String s) {
		if (s != null) {
			return new JsonListModel(s);
		}
		return null;
	}

}
