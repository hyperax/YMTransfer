package ru.yandex.money.ymtransfer.utils_name;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

public class NpeUtils {

    @NonNull
    public static <T> T getNonNull(T value, @NonNull T ifNullValue) {
        return value == null? ifNullValue : value;
    }

    @NonNull
    public static String getNonNull(String value) {
        return getNonNull(value, "");
    }

    @NonNull
    public static long[] getNonNull(long[] value) {
        return getNonNull(value, new long[0]);
    }

    @NonNull
    public static int[] getNonNull(int[] value) {
        return getNonNull(value, new int[0]);
    }

    @NonNull
    public static BigDecimal getNonNull(BigDecimal value) {
        return getNonNull(value, BigDecimal.ZERO);
    }

}
