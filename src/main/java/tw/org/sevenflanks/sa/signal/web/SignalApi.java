package tw.org.sevenflanks.sa.signal.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import tw.org.sevenflanks.sa.base.job.JobQueue;
import tw.org.sevenflanks.sa.signal.model.SignalResultVo;
import tw.org.sevenflanks.sa.signal.service.SignalService;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/signal")
public class SignalApi {

    @Autowired
    private JobQueue jobQueue;

    @Autowired
    private SignalService signalService;

    @GetMapping(value = "/result", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SignalResultVo> result(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate,
            @RequestParam(defaultValue = "0") int page) {
        final LocalDate date = Optional.ofNullable(baseDate).orElseGet(LocalDate::now);
        return Flux.fromIterable(signalService.get(date, PageRequest.of(page, 50)))
                .map(SignalResultVo::new)
                .doOnNext(signalService::info);
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SignalResultVo> run(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate) throws Exception {
        final LocalDate date = Optional.ofNullable(baseDate).orElseGet(LocalDate::now);
        return jobQueue.submit("重跑訊號", () -> Flux.fromIterable(signalService.runAndSave(date))
                .map(SignalResultVo::new));
    }

}
