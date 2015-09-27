package ru.yandex.money.ymtransfer.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.view.fragments.TransferFragment_;
import ru.yandex.money.ymtransfer.view.fragments.TransferListFragment;
import ru.yandex.money.ymtransfer.view.fragments.TransferListFragment_;

@EActivity(R.layout.activity_single_fragment)
public class MainActivity extends AppCompatActivity implements TransferListFragment.Callback, FragmentManager.OnBackStackChangedListener {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void init() {
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        updateIcon();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initFragments(savedInstanceState);
    }

    private void initFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            replaceMainFragment(TransferListFragment_.builder().build());
        }
    }

    private void replaceMainFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void replaceMainFragment(Fragment fragment, String backStackName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(backStackName)
                .commit();
    }

    @Override
    public void onAddTransfer() {
        onOpenTransfer(new Transfer());
    }

    private Fragment getTransferFragment(@Nullable Transfer transfer) {
        return TransferFragment_.builder().transfer(transfer).build();
    }

    @Override
    public void onOpenTransfer(Transfer transfer) {
        replaceMainFragment(getTransferFragment(transfer), "new_transfer");
    }

    @Override
    public void onBackStackChanged() {
        updateIcon();
    }

    protected void updateIcon() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0
                || NavUtils.getParentActivityName(this) != null;
        if (canGoBack) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setNavigationOnClickListener(null);
        }
    }
}
