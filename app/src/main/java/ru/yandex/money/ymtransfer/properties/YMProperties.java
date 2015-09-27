package ru.yandex.money.ymtransfer.properties;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class YMProperties {
    public static final String CLIENT_ID = "B06CB2A09433850862BF5B13BFD474BAFC69DEDEA4244FC41998055B7C3264F5";
    public static final BigDecimal FEE_VALUE = new BigDecimal("0.005");

    public static final String HOST = "https://money.yandex.ru";

    public static BigDecimal addFee(BigDecimal amount) {
        return amount.multiply(FEE_VALUE.add(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal substractFee(BigDecimal amount) {
        return amount.divide(FEE_VALUE.add(BigDecimal.ONE), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}
