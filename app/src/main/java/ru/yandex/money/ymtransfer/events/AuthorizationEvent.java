package ru.yandex.money.ymtransfer.events;

public class AuthorizationEvent extends BaseEvent {
    private String redirectUri;

    public String getRedirectUri() {
        return redirectUri;
    }

    public AuthorizationEvent setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }
}
