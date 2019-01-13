package tw.org.sevenflanks.sa.signal.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.OtcStockDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.OtcStock;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成交量連續 a 個營業日低於近 b 個營業日平均值
 */
@Slf4j
@Component
public class Rule001 extends SignalRule<Rule001.Factor> {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private OtcStockDao otcStockDao;

	@Autowired
	private TwseStockDao twseStockDao;

	@Override
	protected boolean isOtcMatch(String uid, LocalDate baseDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = otcStockDao.findRecordDates(uid, baseDate, factor.a);
		log.debug("checking {}.OTC uid:{}, recordDates:{}", this.code(), uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final OtcStock target = otcStockDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgSharesTraded = otcStockDao.findAvgSharesTraded(uid, date, factor.b);
					final BigDecimal targetSharesTraded = target.getSharesTraded();

					// target日 的成交量是否低於 近 b 個營業日平均值
					final boolean result = targetSharesTraded.compareTo(avgSharesTraded) <= 0;

					log.debug("\tchecking {}.OTC result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", this.code(), result, uid, date, targetSharesTraded, factor.b, avgSharesTraded);
					return result;
				});
	}

	@Override
	protected boolean isTwseMatch(String uid, LocalDate baseDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = twseStockDao.findRecordDates(uid, baseDate, factor.a);
		log.debug("checking {}.TWSE uid:{}, recordDates:{}", this.code(), uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final TwseStock target = twseStockDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgSharesTraded = twseStockDao.findAvgSharesTraded(uid, date, factor.b);
					final BigDecimal targetSharesTraded = target.getSharesTraded();

					// target日 的成交量是否低於 近 b 個營業日平均值
					final boolean result = targetSharesTraded.compareTo(avgSharesTraded) <= 0;

					log.debug("\tchecking {}.TWSE result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", this.code(), result, uid, date, targetSharesTraded, factor.b, avgSharesTraded);
					return result;
				});
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Factor implements SignalRule.Factor {
		private int a;
		private int b;

		@Override
		public String toString() {
			return "Factor{" +
					"a=" + a +
					", b=" + b +
					'}';
		}
	}

}
