package tw.org.sevenflanks.sa.base.utils;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

public abstract class WebFluxUtils {

	public static <T> Flux<ServerSentEvent<T>> SSE(Flux<T> flux) {
		return flux.map(e -> ServerSentEvent.builder(e)
				.build());
	}

	public static <T> ParallelFlux<ServerSentEvent<T>> SSE(ParallelFlux<T> flux) {
		return flux.map(e -> ServerSentEvent.builder(e)
				.build());
	}

}
