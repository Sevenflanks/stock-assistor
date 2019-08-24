package tw.org.sevenflanks.sa.signal.rule;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.signal.model.SignalRunOption;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public abstract class SignalRule<FACTOR extends SignalRule.Factor> {

	public String code() {
		return this.getClass().getSimpleName();
	}

	public Stream<CompanyVo> getMatch(LocalDate baseDate, FACTOR factor, List<OtcCompany> otcCompanies, List<TwseCompany> twseCompanies, SignalRunOption option) {
		log.info("checking {} {}, base:{}", this.code(), factor, baseDate);

		return Stream.concat(
				otcCompanies.stream()
						.peek(option.getBeforeDoMatch())
						.filter(company -> isOtcMatch(company.getUid(), baseDate, factor))
						.map(CompanyVo::new),
				twseCompanies.stream()
						.peek(option.getBeforeDoMatch())
						.filter(company -> isTwseMatch(company.getUid(), baseDate, factor))
						.map(CompanyVo::new)
		);
	}

	protected abstract boolean isOtcMatch(String uid, LocalDate base, FACTOR factor);

	protected abstract boolean isTwseMatch(String uid, LocalDate base, FACTOR factor);

	public interface Factor {

	}

}
