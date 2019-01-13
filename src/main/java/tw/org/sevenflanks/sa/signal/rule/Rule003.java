package tw.org.sevenflanks.sa.signal.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.OtcRgremainDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseRgremainDao;
import tw.org.sevenflanks.sa.stock.entity.OtcRgremain;
import tw.org.sevenflanks.sa.stock.entity.TwseRgremain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 融資餘額連續 a 個營業日低於近 b 個營業日平均值
 */
@Slf4j
@Component
public class Rule003 extends SignalRule<Rule003.Factor> {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private OtcRgremainDao otcRgremainDao;

	@Autowired
	private TwseRgremainDao twseRgremainDao;

	@Override
	protected boolean isOtcMatch(String uid, LocalDate baseDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = otcRgremainDao.findRecordDates(uid, baseDate, factor.a);
		log.debug("checking {}.OTC uid:{}, recordDates:{}", this.code(), uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final OtcRgremain target = otcRgremainDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgBorrowingBalance = otcRgremainDao.findAvgBorrowingBalance(uid, date, factor.b);
					final BigDecimal targetBorrowingBalance = target.getBorrowingBalance();

					// target日 的融資餘額是否低於 近 b 個營業日平均值
					final boolean result = targetBorrowingBalance != null && avgBorrowingBalance != null && targetBorrowingBalance.compareTo(avgBorrowingBalance) <= 0;

					log.debug("\tchecking {}.OTC result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", this.code(), result, uid, date, targetBorrowingBalance, factor.b, avgBorrowingBalance);
					return result;
				});
	}

	@Override
	protected boolean isTwseMatch(String uid, LocalDate baseDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = twseRgremainDao.findRecordDates(uid, baseDate, factor.a);
		log.debug("checking {}.TWSE uid:{}, recordDates:{}", this.code(), uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final TwseRgremain target = twseRgremainDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgBorrowingBalance = twseRgremainDao.findAvgBorrowingBalance(uid, date, factor.b);
					final BigDecimal targetBorrowingBalance = target.getBorrowingBalance();

					// target日 的融資餘額是否低於 近 b 個營業日平均值
					final boolean result = targetBorrowingBalance != null && avgBorrowingBalance != null && targetBorrowingBalance.compareTo(avgBorrowingBalance) <= 0;

					log.debug("\tchecking {}.TWSE result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", this.code(), result, uid, date, targetBorrowingBalance, factor.b, avgBorrowingBalance);
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
