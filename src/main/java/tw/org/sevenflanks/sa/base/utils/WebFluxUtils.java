package tw.org.sevenflanks.sa.base.utils;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

import javax.net.ssl.SSLContext;

public abstract class WebFluxUtils {

	public static <T> Flux<ServerSentEvent<T>> SSE(Flux<T> flux) {
		return flux.map(e -> ServerSentEvent.builder(e)
				.build());
	}

	public static <T> ParallelFlux<ServerSentEvent<T>> SSE(ParallelFlux<T> flux) {
		return flux.map(e -> ServerSentEvent.builder(e)
				.build());
	}

	private static HttpComponentsClientHttpRequestFactory SSL;
	public static HttpComponentsClientHttpRequestFactory SSL() {
		if (SSL == null) {
			try {
				TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
				SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
						NoopHostnameVerifier.INSTANCE);

				Registry<ConnectionSocketFactory> socketFactoryRegistry =
						RegistryBuilder.<ConnectionSocketFactory> create()
								.register("https", sslsf)
								.register("http", new PlainConnectionSocketFactory())
								.build();

				BasicHttpClientConnectionManager connectionManager =
						new BasicHttpClientConnectionManager(socketFactoryRegistry);
				CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
						.setConnectionManager(connectionManager).build();

				SSL = new HttpComponentsClientHttpRequestFactory(httpClient);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return SSL;
	}

}
