package ru.yandex.money.ymtransfer.properties;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface AppPrefs {

    @DefaultString("")
    String token();

}
