package tw.org.sevenflanks.sa.base.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

public class JsonUtils {
    public final static String SPLITER = "::";

    public final static ObjectMapper OM =
            Jackson2ObjectMapperBuilder.json().failOnUnknownProperties(false).build();

    @SuppressWarnings("unchecked")
    public static <T> T readLFormDb(String json) throws Exception {
        if (json != null) {
            final String[] splited = json.split(SPLITER);
            final Class<T> valueClass = (Class<T>) Class.forName(splited[0]);
            return OM.readValue(splited[1], valueClass);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> readListFormDb(String json) throws Exception {
        if (json != null) {
            final String[] splited = json.split(SPLITER);
            final Class<T> valueClass = (Class<T>) Class.forName(splited[0]);
            return OM.readValue(splited[1], OM.getTypeFactory().constructCollectionType(List.class, valueClass));
        }
        return null;
    }

}
