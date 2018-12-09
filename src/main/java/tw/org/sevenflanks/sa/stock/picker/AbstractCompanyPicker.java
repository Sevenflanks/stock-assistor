package tw.org.sevenflanks.sa.stock.picker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import tw.org.sevenflanks.sa.base.msg.enums.MsgTemplate;
import tw.org.sevenflanks.sa.base.msg.exception.MsgException;
import tw.org.sevenflanks.sa.stock.model.TwseCompanyModel;

public interface AbstractCompanyPicker<T> {

	Class<T> modelClass();

	/** 取得所有公司資訊 */
	default List<T> getAll(String apiUrl) {
		try {
			// 建立request
			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl);
			final RestTemplate restTemplate = new RestTemplate();
			final HttpHeaders headers = new HttpHeaders();
			final HttpEntity<?> request = new HttpEntity<>(headers);
			final String url = builder.toUriString();
			final ResponseEntity<Resource> responseEntity = restTemplate.exchange(
					url,
					HttpMethod.GET,
					request,
					Resource.class);

			// 讀取CSV準備
			final ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
			strategy.setType(modelClass());
			final String[] columns = Stream.of(TwseCompanyModel.class.getDeclaredFields())
					.map(Field::getName)
					.toArray(String[]::new);
			strategy.setColumnMapping(columns);

			// 讀取CSV
			final InputStream inputStream = MsgTemplate.requireBody("API", url, responseEntity).getInputStream();
			final CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(new InputStreamReader(inputStream))
					.withMappingStrategy(strategy)
					.withSkipLines(1)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			return csvToBean.parse();
		} catch (IOException e) {
			throw new MsgException(MsgTemplate.SYS9999.build("取得公司資訊失敗"), e);
		}
	}

}
