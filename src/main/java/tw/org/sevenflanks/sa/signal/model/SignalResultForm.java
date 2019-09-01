package tw.org.sevenflanks.sa.signal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignalResultForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate baseDate;

    private List<String> uids;
}
