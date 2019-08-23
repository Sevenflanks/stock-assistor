package tw.org.sevenflanks.sa.signal.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.org.sevenflanks.sa.stock.dao.OtcStockDao;
import tw.org.sevenflanks.sa.stock.dao.TwseStockDao;
import tw.org.sevenflanks.sa.stock.entity.OtcStock;
import tw.org.sevenflanks.sa.stock.entity.TwseStock;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 當日有收盤價
 */
@Slf4j
@Component
public class Rule000 extends SignalRule<Rule000.Factor> {

	@Autowired
	private OtcStockDao otcStockDao;

	@Autowired
	private TwseStockDao twseStockDao;

	@Override
	protected boolean isOtcMatch(String uid, LocalDate baseDate, Factor factor) {
		final OtcStock otcStock = otcStockDao.findByUidAndSyncDate(uid, baseDate);
		return otcStock != null && isClosingPriceMatch(factor, otcStock.getClosingPrice());
	}

	@Override
	protected boolean isTwseMatch(String uid, LocalDate baseDate, Factor factor) {
		final TwseStock twseStock = twseStockDao.findByUidAndSyncDate(uid, baseDate);
		return twseStock != null && isClosingPriceMatch(factor, twseStock.getClosingPrice());
	}

	private boolean isClosingPriceMatch(Factor factor, BigDecimal closingPrice) {
		final boolean isMinMatch = factor.min != null && closingPrice != null && closingPrice.compareTo(factor.min) >= 0;
		final boolean isMaxMatch = factor.max != null && closingPrice != null && closingPrice.compareTo(factor.max) <= 0;
		return isMinMatch || isMaxMatch;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Factor implements SignalRule.Factor {

		private BigDecimal min;
		private BigDecimal max;

		@Override
		public String toString() {
			return "Factor{" +
					"min=" + min +
					", max=" + max +
					'}';
		}
	}

}
