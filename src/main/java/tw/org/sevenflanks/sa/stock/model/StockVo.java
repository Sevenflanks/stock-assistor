package tw.org.sevenflanks.sa.stock.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockVo {

    /** 收盤 */
    private BigDecimal closingPrice;

    /** 漲跌 */
    private String upsDowns;

    /** 開盤 */
    private BigDecimal openingPrice;

    /** 最高 */
    private BigDecimal highestPrice;

    /** 最低 */
    private BigDecimal lowestPrice;

}
