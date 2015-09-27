package ru.yandex.money.ymtransfer.data.contract;

public interface Contract {

    String ID = "_id";

    interface Transfer {

        String RECIPIENT = "recipient";
        String AMOUNT_PAYMENT = "amount_payment";
        String AMOUNT_DUE = "amount_due";
        String IS_CODE_PROTECTION = "is_code_pro";
        String RECEIVE_PERIOD = "receive_period";
        String STATUS = "status";
        String COMMENT = "comment";
        String DATE_CREATION = "date_creation";
    }

}
