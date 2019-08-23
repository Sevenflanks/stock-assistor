package tw.org.sevenflanks.sa.base.job;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * 用於處理工作排隊用的Queue
 * 主要目的用於避免同樣的工作被同時觸發, 並控管同時間進行中的工作數
 */
@Component
public class JobQueue {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<String> jobs = Lists.newArrayList();

    public <T> T submit(String jobId, Callable<T> callable) throws Exception {
        synchronized (jobs) {
            if (jobs.contains(jobId)) {
                throw new RuntimeException("批次作業【" + jobId + "】已經正在處理中，請稍後");
            }
            jobs.add(jobId);
        }

        final T result;
        try {
            final Future<T> future = executor.submit(callable);
            result = future.get();

        } finally {
            synchronized (jobs) {
                jobs.remove(jobId);
            }
        }

        return result;
    }

}
