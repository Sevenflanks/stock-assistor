package tw.org.sevenflanks.sa.signal.rule;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.OtcRgremainDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseRgremainDao;
import tw.org.sevenflanks.sa.stock.entity.OtcRgremain;
import tw.org.sevenflanks.sa.stock.entity.TwseRgremain;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 融資餘額連續 a 個營業日低於近 b 個營業日平均值
 */
@Slf4j
@Component
public class Sng003 implements SingleRule<Sng003.Factor> {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private OtcRgremainDao otcRgremainDao;

	@Autowired
	private TwseRgremainDao twseRgremainDao;

	@Override
	public List<CompanyVo> getMatch(Factor factor) {
		final LocalDate now = LocalDate.now();
		log.info("checking Sng001 {}, start:{}", factor, now);

		return Stream.concat(
				otcCompanyDao.findByLastSyncDate().stream()
						.filter(company -> isOtcMatch(company.getUid(), now, factor))
						.map(CompanyVo::new),
				twseCompanyDao.findByLastSyncDate().stream()
						.filter(company -> isTwseMatch(company.getUid(), now, factor))
						.map(CompanyVo::new)

		).collect(Collectors.toList());
	}

	private boolean isOtcMatch(String uid, LocalDate startDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = otcRgremainDao.findRecordDates(uid, startDate, factor.a);
		log.debug("checking Sng003.OTC uid:{}, recordDates:{}", uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final OtcRgremain target = otcRgremainDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgBorrowingBalance = otcRgremainDao.findAvgBorrowingBalance(uid, date, factor.b);
					final BigDecimal targetBorrowingBalance = target.getBorrowingBalance();

					// target日 的融資餘額是否低於 近 b 個營業日平均值
					final boolean result = targetBorrowingBalance != null && avgBorrowingBalance != null && targetBorrowingBalance.compareTo(avgBorrowingBalance) <= 0;

					log.debug("\tchecking Sng003.OTC result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", result, uid, date, targetBorrowingBalance, factor.b, avgBorrowingBalance);
					return result;
				});
	}

	private boolean isTwseMatch(String uid, LocalDate startDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = twseRgremainDao.findRecordDates(uid, startDate, factor.a);
		log.debug("checking Sng003.TWSE uid:{}, recordDates:{}", uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final TwseRgremain target = twseRgremainDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgBorrowingBalance = twseRgremainDao.findAvgBorrowingBalance(uid, date, factor.b);
					final BigDecimal targetBorrowingBalance = target.getBorrowingBalance();

					// target日 的融資餘額是否低於 近 b 個營業日平均值
					final boolean result = targetBorrowingBalance != null && avgBorrowingBalance != null && targetBorrowingBalance.compareTo(avgBorrowingBalance) <= 0;

					log.debug("\tchecking Sng003.TWSE result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", result, uid, date, targetBorrowingBalance, factor.b, avgBorrowingBalance);
					return result;
				});
	}

	@Builder
	static class Factor {
		private final int a;
		private final int b;

		@Override
		public String toString() {
			return "Factor{" +
					"a=" + a +
					", b=" + b +
					'}';
		}
	}

}
