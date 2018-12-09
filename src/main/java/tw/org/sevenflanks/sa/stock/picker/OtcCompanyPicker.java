package tw.org.sevenflanks.sa.stock.picker;

import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.stock.model.OtcCompanyModel;

@Slf4j
@Component
public class OtcCompanyPicker implements AbstractCompanyPicker<OtcCompanyModel> {

	@Override
	public Class<OtcCompanyModel> modelClass() {
		return OtcCompanyModel.class;
	}

	/** 取得所有上櫃公司資訊 */
	@Retryable(maxAttempts = 3, backoff = @Backoff(value = 5000))
	public List<OtcCompanyModel> getAll() {
		return getAll("http://mopsfin.twse.com.tw/opendata/t187ap03_O.csv");
	}
}
