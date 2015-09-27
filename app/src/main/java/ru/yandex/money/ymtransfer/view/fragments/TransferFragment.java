package ru.yandex.money.ymtransfer.view.fragments;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import ru.softbalance.widgets.NumberEditText;
import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.data.sqlite.Storage;

@EFragment(R.layout.fragment_transfer)
public class TransferFragment extends BaseFragment {

    @ViewById(R.id.recipient)
    EditText recipient;

    @ViewById(R.id.amount_pay)
    NumberEditText amountPay;

    @ViewById(R.id.amount_due)
    NumberEditText amountDue;

    @ViewById(R.id.comment)
    EditText comment;

    @ViewById(R.id.use_code_protection)
    CheckBox useCodeProtection;

    @ViewById(R.id.receive_period)
    NumberEditText receivePeriod;

    @FragmentArg
    @InstanceState
    Transfer transfer;

    @AfterViews
    void init() {
        initPaymentData();
        initPaymentView();
    }

    private void initPaymentData() {
        if (transfer == null) {
            transfer = new Transfer();
        }
    }

    private void initPaymentView() {
        recipient.setText(transfer.getRecipient());
        amountPay.setValue(transfer.getAmountPayment());
        amountDue.setValue(transfer.getAmountDue());
        comment.setText(transfer.getComment());
        useCodeProtection.setChecked(transfer.isCodeProtection());
        receivePeriod.setText(String.valueOf(transfer.getReceivePeriod()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getPaymentFromView();
    }

    private void getPaymentFromView() {
        transfer.setRecipient(recipient.getText().toString().trim());
        transfer.setAmountPayment(amountPay.getValue());
        transfer.setAmountDue(amountDue.getValue());
        transfer.setComment(comment.getText().toString().trim());
        transfer.setIsCodeProtection(useCodeProtection.isChecked());
        transfer.setReceivePeriod(receivePeriod.getValue().intValue());
    }

    @Click(R.id.payment)
    void preparePayment() {
        getPaymentFromView();
        savePayment();
        startPayment();
    }

    private void savePayment() {
        Storage.get().put(transfer);
    }

    private void startPayment() {

    }
}
