package tw.org.sevenflanks.sa.stock.picker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.model.TwseCompanyModel;

import java.util.List;

@Slf4j
@Component
public class TwseCompanyPicker implements AbstractCompanyPicker<TwseCompanyModel> {

	@Override
	public Class<TwseCompanyModel> modelClass() {
		return TwseCompanyModel.class;
	}

	/** 取得所有上市公司資訊 */
	@Retryable(maxAttempts = 3, backoff = @Backoff(value = 5000))
	public List<TwseCompanyModel> getAll() {
		return getAll("https://mopsfin.twse.com.tw/opendata/t187ap03_L.csv");
	}
}
