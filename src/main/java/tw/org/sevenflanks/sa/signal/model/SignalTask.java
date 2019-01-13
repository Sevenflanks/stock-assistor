package tw.org.sevenflanks.sa.signal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tw.org.sevenflanks.sa.signal.entity.Signal;
import tw.org.sevenflanks.sa.stock.model.CompanyVo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@AllArgsConstructor
public class SignalTask {

	private Signal signal;

	private CompletableFuture<List<CompanyVo>> future;

}
