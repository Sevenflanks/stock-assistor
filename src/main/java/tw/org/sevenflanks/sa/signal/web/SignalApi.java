package tw.org.sevenflanks.sa.signal.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import tw.org.sevenflanks.sa.base.job.JobQueue;
import tw.org.sevenflanks.sa.base.utils.WebFluxUtils;
import tw.org.sevenflanks.sa.signal.model.SignalResultVo;
import tw.org.sevenflanks.sa.signal.service.SignalService;

@Slf4j
@RestController
@RequestMapping("/api/signal")
public class SignalApi {

    @Autowired
    private JobQueue jobQueue;

    @Autowired
    private SignalService signalService;

    @GetMapping("/result")
    public Flux<ServerSentEvent<SignalResultVo>> result(@RequestParam(defaultValue = "0") int page) {
        return WebFluxUtils.SSE(Flux.fromIterable(signalService.get(PageRequest.of(page, 50)))
                .map(SignalResultVo::new)
                .doOnNext(signalService::info));
    }

    @PostMapping
    public Flux<ServerSentEvent<SignalResultVo>> run() throws Exception {
        return jobQueue.submit("重跑訊號", () -> WebFluxUtils.SSE(Flux.fromIterable(signalService.runAndSave())
                .map(SignalResultVo::new)));
    }

}
