package tw.org.sevenflanks.sa.signal.rule;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.dao.OtcCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.OtcStockDao;
import tw.org.sevenflanks.sa.stock.dao.TwseCompanyDao;
import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.OtcStock;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 成交量連續 a 個營業日低於近 b 個營業日平均值
 */
@Slf4j
@Component
public class Sng001 implements SingleRule<Sng001.Factor> {

	@Autowired
	private OtcCompanyDao otcCompanyDao;

	@Autowired
	private TwseCompanyDao twseCompanyDao;

	@Autowired
	private OtcStockDao otcStockDao;

	@Autowired
	private TwseStockDao twseStockDao;

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
		final List<LocalDate> recordDates = otcStockDao.findRecordDates(uid, startDate, factor.a).stream().map(Date::toLocalDate).collect(Collectors.toList());
		log.debug("checking Sng001.OTC uid:{}, recordDates:{}", uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final OtcStock target = otcStockDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgSharesTraded = otcStockDao.findAvgSharesTraded(uid, date, factor.b);
					final BigDecimal targetSharesTraded = target.getSharesTraded();

					// target日 的成交量是否低於 近 b 個營業日平均值
					final boolean result = targetSharesTraded.compareTo(avgSharesTraded) <= 0;

					log.debug("\tchecking Sng001.OTC result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", result, uid, date, targetSharesTraded, factor.b, avgSharesTraded);
					return result;
				});
	}

	private boolean isTwseMatch(String uid, LocalDate startDate, Factor factor) {
		// 連續 a 個營業日
		final List<LocalDate> recordDates = twseStockDao.findRecordDates(uid, startDate, factor.a).stream().map(Date::toLocalDate).collect(Collectors.toList());
		log.debug("checking Sng001.TWSE uid:{}, recordDates:{}", uid, recordDates.stream().map(LocalDate::toString).collect(Collectors.joining(",")));
		return recordDates.stream()
				.allMatch(date -> {
					final TwseStock target = twseStockDao.findByUidAndSyncDate(uid, date);
					final BigDecimal avgSharesTraded = twseStockDao.findAvgSharesTraded(uid, date, factor.b);
					final BigDecimal targetSharesTraded = target.getSharesTraded();

					// target日 的成交量是否低於 近 b 個營業日平均值
					final boolean result = targetSharesTraded.compareTo(avgSharesTraded) <= 0;

					log.debug("\tchecking Sng001.TWSE result:{}, uid:{}, a.date:{}, a.target:{}, avg.in.{}:{}", result, uid, date, targetSharesTraded, factor.b, avgSharesTraded);
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
