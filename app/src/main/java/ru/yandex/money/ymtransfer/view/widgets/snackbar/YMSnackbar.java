package ru.yandex.money.ymtransfer.view.widgets.snackbar;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.utils.ViewUtils;

public class YMSnackbar {

    private YMSnackbar() {
    }

    public static void showMessage(Activity activity, @Nullable View view, Message mes) {

        @SuppressWarnings("ResourceType")
        Snackbar sb = Snackbar.make(
                view != null ? view : activity.findViewById(android.R.id.content),
                mes.getText(),
                mes.getDuration());

        View snackBarView = sb.getView();

        int mesColorRes;
        switch (mes.getType()) {
            case Message.ALERT:
                mesColorRes = R.color.message_alert;
                break;
            case Message.CONFIRM:
                mesColorRes = R.color.message_confirm;
                break;
            default:
                mesColorRes = R.color.message_info;
                break;
        }

        snackBarView.setBackgroundColor(ViewUtils.getColor(activity, mesColorRes));
        sb.show();
    }
}
