package tw.org.sevenflanks.sa.signal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.org.sevenflanks.sa.signal.entity.Signal;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SigalResult {

	private CompanyVo company;

	private List<Signal> matchs;

}
