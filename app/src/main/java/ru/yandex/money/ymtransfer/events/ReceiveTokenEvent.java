package ru.yandex.money.ymtransfer.events;

import com.yandex.money.api.methods.Token;

public class ReceiveTokenEvent extends BaseEvent {

    private Token token;

    public Token getToken() {
        return token;
    }

    public ReceiveTokenEvent setToken(Token token) {
        this.token = token;
        return this;
    }
}
