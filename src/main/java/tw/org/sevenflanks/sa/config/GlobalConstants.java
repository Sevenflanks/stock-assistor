package tw.org.sevenflanks.sa.config;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalConstants {

	public static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd['('eeeee')']").withLocale(Locale.TAIWAN);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd['('eeeee')'] HH:mm").withLocale(Locale.TAIWAN);

	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public static ObjectMapper WEB_OBJECT_MAPPER;

}
