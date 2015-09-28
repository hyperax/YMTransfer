package ru.yandex.money.ymtransfer.events;

import com.yandex.money.api.methods.ProcessPayment;

import ru.yandex.money.ymtransfer.data.model.Transfer;

public class PaymentEvent extends BaseEvent {

    private Transfer transfer;
    private ProcessPayment processPayment;

    public PaymentEvent(Transfer transfer, ProcessPayment processPayment) {
        this.transfer = transfer;
        this.processPayment = processPayment;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public ProcessPayment getProcessPayment() {
        return processPayment;
    }
}
