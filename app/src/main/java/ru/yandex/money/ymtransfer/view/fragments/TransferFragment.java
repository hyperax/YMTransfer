package ru.yandex.money.ymtransfer.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.yandex.money.api.methods.params.P2pTransferParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import ru.softbalance.widgets.NumberEditText;
import ru.yandex.money.android.PaymentActivity;
import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.data.sqlite.Storage;
import ru.yandex.money.ymtransfer.properties.YMProperties;

@EFragment(R.layout.fragment_transfer)
public class TransferFragment extends BaseFragment {

    private static final int REQUEST_PAYMENT = 1;

    @ViewById(R.id.recipient)
    EditText recipient;

    @ViewById(R.id.amount_pay)
    NumberEditText amountPay;

    @ViewById(R.id.amount_due)
    NumberEditText amountDue;

    @ViewById(R.id.use_code_protection)
    CheckBox useCodeProtection;

    @ViewById(R.id.comment)
    EditText comment;

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
        transfer.setIsCodeProtection(useCodeProtection.isChecked());
        transfer.setComment(comment.getText().toString().trim());
        transfer.setReceivePeriod(receivePeriod.getValue().intValue());
    }

    @Click(R.id.payment)
    void preparePayment() {
        getPaymentFromView();
        savePayment();
        startPayment();
    }

    @FocusChange(R.id.amount_pay)
    void onAmountPayChanged(boolean isFocused, View view) {
        if (!isFocused) {
            amountDue.setValue(YMProperties.substractFee(amountPay.getValue()));
        }
    }

    @FocusChange(R.id.amount_due)
    void onAmountDueChanged(boolean isFocused, View view) {
        if (!isFocused) {
            amountPay.setValue(YMProperties.addFee(amountDue.getValue()));
        }
    }

    private void savePayment() {
        Storage.get().put(transfer);
    }

    private void startPayment() {
        P2pTransferParams payParameters = new P2pTransferParams.Builder(transfer.getRecipient())
                .setAmount(transfer.getAmountPayment())
                .setCodepro(transfer.isCodeProtection())
                .setMessage(transfer.getComment())
                .setExpirePeriod(transfer.getReceivePeriod())
                .build();
        Intent intent = PaymentActivity.getBuilder(getActivity())
                .setPaymentParams(payParameters)
                .setClientId(YMProperties.CLIENT_ID)
                .setHost(YMProperties.HOST)
                .build();
        startActivityForResult(intent, REQUEST_PAYMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), R.string.transfer_completed, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), R.string.transfer_incompleted, Toast.LENGTH_LONG).show();
        }

    }
}
