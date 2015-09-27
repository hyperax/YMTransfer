package ru.yandex.money.ymtransfer.data.model.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        PaymentStatus.INCOMPLETE,
        PaymentStatus.COMPLETE,
        PaymentStatus.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface PaymentStatus {
    int INCOMPLETE = 0;
    int COMPLETE = 1;
    int ERROR = 2;
}
