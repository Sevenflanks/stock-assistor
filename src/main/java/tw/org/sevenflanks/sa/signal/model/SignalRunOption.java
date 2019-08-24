package tw.org.sevenflanks.sa.signal.model;

import lombok.*;
import tw.org.sevenflanks.sa.stock.entity.Company;

import java.util.Optional;
import java.util.function.Consumer;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalRunOption {

    private static final Consumer<Company> DO_NOTHING = c -> {};

    private Consumer<Company> beforeDoMatch;

    public Consumer<Company> getBeforeDoMatch() {
        return Optional.ofNullable(beforeDoMatch).orElse(DO_NOTHING);
    }
}
