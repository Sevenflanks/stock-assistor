package tw.org.sevenflanks.sa.signal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.org.sevenflanks.sa.signal.rule.SignalRule;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SignalService {

	private Map<String, SignalRule<?>> rules;

	@Autowired
	public void setRules(List<SignalRule<?>> rules) {
		this.rules = rules.stream().collect(Collectors.toMap(SignalRule::code, Function.identity(), (o1, o2) -> {
			throw new RuntimeException("存在兩個以上相同代號的的rule: " + o1.code() + ":" + o1 + ", " + o2.code() + ":" + o2);
		}));
	}

}
