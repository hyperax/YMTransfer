package ru.yandex.money.ymtransfer.view.fragments;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.contract.Contract;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.data.sqlite.Storage;
import ru.yandex.money.ymtransfer.view.adapters.TransferAdapter;

@EFragment(R.layout.fragment_transfer_list)
public class TransferListFragment extends BaseFragment {

    public interface Callback {
        void onAddTransfer();
        void onOpenTransfer(Transfer transfer);
    }

    @ViewById(R.id.transfers_list)
    ListView transfersList;

    @ViewById(R.id.empty_view)
    View emptyView;

    private Callback listener;

    @AfterViews
    void init() {
        initTransfersList();
    }

    private void initTransfersList() {
        transfersList.setEmptyView(emptyView);
    }

    private List<Transfer> getTransfers() {
        return Storage.get()
                .getQuery(Transfer.class)
                .orderBy(Contract.Transfer.DATE_CREATION + " desc")
                .list();
    }

    @ItemClick(R.id.transfers_list)
    void itemClick(Transfer transfer) {
        listener.onOpenTransfer(transfer);
    }

    @Click(R.id.fab)
    void addTransfer() {
        listener.onAddTransfer();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getHostParent() instanceof Callback) {
            listener = (Callback) getHostParent();
        } else {
            throw new ClassCastException("Host activity or parent fragment must implements "
                    + Callback.class.getCanonicalName() + " interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateListAdapter();
    }

    private void updateListAdapter() {
        TransferAdapter adapter = (TransferAdapter) transfersList.getAdapter();
        if (adapter == null) {
            transfersList.setAdapter(new TransferAdapter(getActivity(), getTransfers()));
        } else {
            adapter.setItems(getTransfers());
        }
    }
}
