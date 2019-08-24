package tw.org.sevenflanks.sa.signal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class SignalProgress {

    public final static Map<String, SignalProgressPhase> PROGRESSES = new Hashtable<>();
    public final static String RUN_SIGNAL_PRIFIX = "RUN_SIGNAL_";

    private String key = UUID.randomUUID().toString();

    private int progress;

    private int total;

    private String message;

    public void add(int i) {
        progress += i;
    }

    public static SignalProgress start(String key, int total, String message) {
        final SignalProgress signalProgress = new SignalProgress();
        if (PROGRESSES.containsKey(key)) {
            PROGRESSES.get(key).add(signalProgress);
        } else {
            final SignalProgressPhase signalProgressPhase = new SignalProgressPhase();
            PROGRESSES.put(key, signalProgressPhase);
            signalProgressPhase.add(signalProgress);
        }
        signalProgress.setProgress(0);
        signalProgress.setTotal(total);
        signalProgress.setMessage(message);
        return signalProgress;
    }

    public static void finish(String key) {
        PROGRESSES.remove(key);
    }

    public static SignalProgressPhase get(String key) {
        return Optional.ofNullable(PROGRESSES.get(key))
                .orElseGet(() -> new SignalProgressPhase(true));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignalProgressPhase {
        List<SignalProgress> progresses = new ArrayList<>();

        private boolean complete = false;

        public SignalProgressPhase(boolean complete) {
            this.complete = complete;
        }

        public void add(SignalProgress signalProgress) {
            progresses.add(signalProgress);
        }
    }

}
