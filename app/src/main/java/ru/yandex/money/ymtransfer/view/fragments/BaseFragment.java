package ru.yandex.money.ymtransfer.view.fragments;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected Object getHostParent() {
        Object hostParent = null;
        if (getParentFragment() != null) {
            hostParent = getParentFragment();
        } else if (getActivity() != null) {
            hostParent = getActivity();
        }
        return hostParent;
    }

}
