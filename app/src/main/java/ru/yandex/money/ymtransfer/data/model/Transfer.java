package ru.yandex.money.ymtransfer.data.model;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;

import nl.qbusict.cupboard.annotation.Column;
import ru.yandex.money.ymtransfer.data.contract.Contract;
import ru.yandex.money.ymtransfer.data.model.constants.PaymentStatus;
import ru.yandex.money.ymtransfer.Utils.MathUtils;
import ru.yandex.money.ymtransfer.Utils.NpeUtils;

public class Transfer implements Serializable {

    @Column(Contract.ID)
    private Long id;

    @Column(Contract.Transfer.RECIPIENT)
    private String recipient;

    @Column(Contract.Transfer.AMOUNT_PAYMENT)
    private String amountPayment;

    @Column(Contract.Transfer.AMOUNT_DUE)
    private String amountDue;

    @Column(Contract.Transfer.COMMENT)
    private String comment;

    @Column(Contract.Transfer.IS_CODE_PROTECTION)
    private boolean isCodeProtection;

    @Column(Contract.Transfer.RECEIVE_PERIOD)
    private int receivePeriod;

    @Column(Contract.Transfer.STATUS)
    @PaymentStatus
    private int status;

    @Column(Contract.Transfer.DATE_CREATION)
    private long dateCreation;

    @Column(Contract.Transfer.PROTECTION_CODE)
    private String protectionCode;

    public Transfer() {
        setId(null);
        setRecipient("");
        setAmountPayment(BigDecimal.ZERO);
        setAmountDue(BigDecimal.ZERO);
        setIsCodeProtection(false);
        setReceivePeriod(1);
        setStatus(PaymentStatus.INCOMPLETE);
        setDateCreation(DateTime.now());
    }

    @NonNull
    public BigDecimal getAmountDue() {
        return MathUtils.getValue(amountDue);
    }

    public Transfer setAmountDue(BigDecimal amountDue) {
        this.amountDue = NpeUtils.getNonNull(amountDue).toPlainString();
        return this;
    }

    @NonNull
    public BigDecimal getAmountPayment() {
        return MathUtils.getValue(amountPayment);
    }

    public Transfer setAmountPayment(BigDecimal amountPayment) {
        this.amountPayment = NpeUtils.getNonNull(amountPayment).toPlainString();
        return this;
    }

    public Long getId() {
        return id;
    }

    public Transfer setId(Long id) {
        this.id = id;
        return this;
    }

    public boolean isCodeProtection() {
        return isCodeProtection;
    }

    public Transfer setIsCodeProtection(boolean isCodeProtection) {
        this.isCodeProtection = isCodeProtection;
        return this;
    }

    public int getReceivePeriod() {
        return receivePeriod;
    }

    public Transfer setReceivePeriod(int receivePeriod) {
        this.receivePeriod = receivePeriod;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public Transfer setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    @PaymentStatus
    public int getStatus() {
        return status;
    }

    public Transfer setStatus(@PaymentStatus int status) {
        this.status = status;
        return this;
    }

    @NonNull
    public String getComment() {
        return NpeUtils.getNonNull(comment);
    }

    public Transfer setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @NonNull
    public DateTime getDateCreation() {
        return new DateTime(dateCreation);
    }

    public Transfer setDateCreation(DateTime date) {
        this.dateCreation = NpeUtils.getNonNull(date, new DateTime(0L)).getMillis();
        return this;
    }

    public void setProtectionCode(String protectionCode) {
        this.protectionCode = protectionCode;
    }

    @NonNull
    public String getProtectionCode() {
        return NpeUtils.getNonNull(protectionCode);
    }
}
