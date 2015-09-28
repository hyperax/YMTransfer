package ru.yandex.money.ymtransfer.Utils;

import android.app.Activity;

public class ViewUtils {

    private ViewUtils() {
    }

    @SuppressWarnings("deprecation")
    public static int getColor(Activity activity, int colorResId) {
        int resultColor;
        if(android.os.Build.VERSION.SDK_INT >= 23){
            resultColor = activity.getResources().getColor(colorResId, activity.getTheme());
        } else {
            resultColor = activity.getResources().getColor(colorResId);
        }

        return resultColor;
    }
}
