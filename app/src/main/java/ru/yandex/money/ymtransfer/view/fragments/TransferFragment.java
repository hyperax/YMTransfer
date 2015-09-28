package ru.yandex.money.ymtransfer.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import ru.softbalance.widgets.NumberEditText;
import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.data.model.Transfer;
import ru.yandex.money.ymtransfer.data.model.constants.PaymentStatus;
import ru.yandex.money.ymtransfer.data.sqlite.Storage;
import ru.yandex.money.ymtransfer.events.PaymentEvent;
import ru.yandex.money.ymtransfer.properties.AppPrefs_;
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

    @ViewById(R.id.protection_code)
    EditText protectionCode;

    @ViewById(R.id.comment)
    EditText comment;

    @ViewById(R.id.receive_period)
    NumberEditText receivePeriod;

    @ViewById(R.id.payment)
    Button paymentButton;

    @FragmentArg
    @InstanceState
    Transfer transfer;

    @Pref
    AppPrefs_ prefs;

    private OAuth2Session session = new OAuth2Session(new DefaultApiClient(YMProperties.CLIENT_ID));

    @AfterViews
    void init() {
        initData();
        initViews();
    }

    private void initData() {
        if (transfer == null) {
            transfer = new Transfer();
        }
    }

    private void initViews() {
        boolean isEditable = transfer.getStatus() != PaymentStatus.COMPLETE;

        recipient.setText(transfer.getRecipient());
        recipient.setEnabled(isEditable);

        amountPay.setValue(transfer.getAmountPayment());
        amountPay.setEnabled(isEditable);

        amountDue.setValue(transfer.getAmountDue());
        amountDue.setEnabled(isEditable);

        comment.setText(transfer.getComment());
        comment.setEnabled(isEditable);

        useCodeProtection.setChecked(transfer.isCodeProtection());
        useCodeProtection.setEnabled(isEditable);

        protectionCode.setText(transfer.getProtectionCode());
        receivePeriod.setText(String.valueOf(transfer.getReceivePeriod()));

        receivePeriod.setEnabled(isEditable);

        paymentButton.setEnabled(isEditable);
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
        initViews();
    }

    private void startPayment() {
        new PaymentTask().execute(transfer);
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

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(PaymentEvent event) {
        if (!event.isHandled()) {
            if (event.getProcessPayment() != null) {
                this.transfer = event.getTransfer();
                transfer.setStatus(PaymentStatus.COMPLETE);
                Toast.makeText(getActivity(), R.string.transfer_completed, Toast.LENGTH_LONG).show();
            } else {
                transfer.setStatus(PaymentStatus.ERROR);
                Toast.makeText(getActivity(), R.string.transfer_incompleted, Toast.LENGTH_LONG).show();
            }
            savePayment();
        }
        event.setHandled(true);
        EventBus.getDefault().removeStickyEvent(event);
    }

    private class PaymentTask extends AsyncTask<Transfer, Void, Void> {

        @Override
        protected Void doInBackground(Transfer... params) {

            Transfer transfer = params[0];

            P2pTransferParams transferParams = new P2pTransferParams.Builder(transfer.getRecipient())
                    .setAmountDue(transfer.getAmountDue())
                    .setCodepro(transfer.isCodeProtection())
                    .setComment(transfer.getComment())
                    .setExpirePeriod(transfer.getReceivePeriod())
                    .build();

            RequestPayment.Request request = RequestPayment.Request.newInstance(transferParams);
            RequestPayment requestPayment;
            ProcessPayment processPayment = null;
            session.setAccessToken(prefs.token().get());

            if (session.isAuthorized()) {
                try {
                    requestPayment = session.execute(request);
                    if (requestPayment.requestId != null && !TextUtils.isEmpty(requestPayment.requestId)) {
                        ProcessPayment.Request requestProcess = new ProcessPayment.Request(requestPayment.requestId);
                        transfer.setProtectionCode(requestPayment.protectionCode);
                        processPayment = session.execute(requestProcess);
                    }
                } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
                    e.printStackTrace();
                }
            }

            EventBus.getDefault().postSticky(new PaymentEvent(transfer, processPayment));

            return null;
        }
    }
}
