package tw.org.sevenflanks.sa.signal.rule;

import lombok.extern.slf4j.Slf4j;
import tw.org.sevenflanks.sa.stock.entity.OtcCompany;
import tw.org.sevenflanks.sa.stock.entity.TwseCompany;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class SignalRule<FACTOR extends SignalRule.Factor> {

	public String code() {
		return this.getClass().getSimpleName();
	}


	public List<CompanyVo> getMatch(FACTOR factor, List<OtcCompany> otcCompanies, List<TwseCompany> twseCompanies) {
		final LocalDate now = LocalDate.now();
		log.info("checking {} {}, base:{}", this.code(), factor, now);

		return Stream.concat(
				otcCompanies.stream()
						.peek(company -> {})
						.filter(company -> isOtcMatch(company.getUid(), now, factor))
						.map(CompanyVo::new),
				twseCompanies.stream()
						.filter(company -> isTwseMatch(company.getUid(), now, factor))
						.map(CompanyVo::new)

		).collect(Collectors.toList());
	}

	protected abstract boolean isOtcMatch(String uid, LocalDate base, FACTOR factor);

	protected abstract boolean isTwseMatch(String uid, LocalDate base, FACTOR factor);

	public interface Factor {

	}

}
