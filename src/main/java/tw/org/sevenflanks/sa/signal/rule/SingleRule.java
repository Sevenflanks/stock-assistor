package tw.org.sevenflanks.sa.signal.rule;

import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.util.List;

public interface SingleRule<FACTOR> {

	default String code() {
		return this.getClass().getSimpleName();
	}

	List<CompanyVo> getMatch(FACTOR factor);

}
