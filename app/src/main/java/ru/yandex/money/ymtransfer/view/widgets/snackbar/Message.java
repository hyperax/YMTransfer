package ru.yandex.money.ymtransfer.view.widgets.snackbar;

import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Message implements Serializable {

    public static final int INFO = 1;
    public static final int CONFIRM = 2;
    public static final int ALERT = 4;
    @IntDef({INFO, CONFIRM, ALERT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    public static final int SHORT = Snackbar.LENGTH_SHORT;
    public static final int LONG = Snackbar.LENGTH_LONG;
    public static final int INDEFINITE = Snackbar.LENGTH_INDEFINITE;
    @IntDef({SHORT, LONG, INDEFINITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}

    @Type
    private int type = INFO;

    @Duration
    private int duration = SHORT;

    private String text;

    @Type
    public int getType() {
        return type;
    }

    public Message setType(@Type int type) {
        this.type = type;
        return this;
    }

    @Duration
    public int getDuration() {
        return duration;
    }

    public Message setDuration(@Duration int duration) {
        this.duration = duration;
        return this;
    }

    public String getText() {
        return text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }
}
