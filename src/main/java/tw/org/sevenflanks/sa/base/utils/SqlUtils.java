package tw.org.sevenflanks.sa.base.utils;

import com.google.common.base.CaseFormat;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class SqlUtils {

  @SneakyThrows
  public static <T> String batchInsert(Class<T> entityClass) {
    String tableName = getTableName(entityClass);

    List<Field> columnFields = getAllSuperclasses(entityClass).stream()
        .map(Class::getDeclaredFields)
        .flatMap(Stream::of)
        .filter(field -> field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class))
        .collect(Collectors.toList());

    String columnNames = columnFields.stream()
        .map(SqlUtils::getColumnName)
        .collect(Collectors.joining(","));
    String fieldNames = columnFields.stream()
        .map(field -> ":" + field.getName())
        .collect(Collectors.joining(","));

    return "INSERT INTO " + tableName + "(" + columnNames + ") VALUES (" + fieldNames + ")";
  }

  public static <T> MapSqlParameterSource[] getBatchValuesToMap(Collection<T> entitys, Consumer<T> perEntityTodo) {
    return entitys.stream().map((entity) -> {
      perEntityTodo.accept(entity);
      Jdk8MapSqlParameterSource parameterSource = new Jdk8MapSqlParameterSource();
      (new BeanMap(entity)).forEach((key, value) -> {
        parameterSource.addValue(String.valueOf(key), value);
      });
      return parameterSource;
    }).toArray(Jdk8MapSqlParameterSource[]::new);
  }

  private static String getTableName(Class<?> entityClass) {
    return Optional.ofNullable(entityClass.getAnnotation(Table.class))
        .map(Table::name)
        .orElseGet(() -> CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, entityClass.getSimpleName()));
  }

  private static String getColumnName(Field field) {
    return Optional.ofNullable(field.getAnnotation(Table.class))
        .map(Table::name)
        .orElseGet(() -> CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, field.getName()));
  }

  private static List<Class<?>> getAllSuperclasses(Class<?> cls) {
    List<Class<?>> classes = new ArrayList<>();
    classes.add(cls);
    for(Class superclass = cls.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
      classes.add(superclass);
    }
    return classes;
  }

  static class Jdk8MapSqlParameterSource extends MapSqlParameterSource {
    private BiFunction<String, Object, Object> mapper;

    public Jdk8MapSqlParameterSource() {
    }

    public Jdk8MapSqlParameterSource(String paramName, Object value) {
      super(paramName, value);
    }

    public Jdk8MapSqlParameterSource(Map<String, ?> values) {
      super(values);
    }

    public void registerMapper(BiFunction<String, Object, Object> mapper) {
      this.mapper = mapper;
    }

    public Object getValue(String paramName) {
      Object result = super.getValue(paramName);
      if (result != null) {
        if (result instanceof LocalDate) {
          result = Date.from(((LocalDate)result).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (result instanceof LocalDateTime) {
          result = Date.from(((LocalDateTime)result).atZone(ZoneId.systemDefault()).toInstant());
        } else if (result instanceof Enum) {
          result = ((Enum)result).name();
        } else if (result instanceof Collection) {
          result = CollectionUtils.isEmpty((Collection)result) ? null : result;
        }
      }

      if (this.mapper != null) {
        result = this.mapper.apply(paramName, result);
      }

      return result;
    }
  }


}
