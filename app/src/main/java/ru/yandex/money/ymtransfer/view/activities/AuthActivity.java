package ru.yandex.money.ymtransfer.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.net.AuthorizationCodeResponse;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.net.URISyntaxException;

import de.greenrobot.event.EventBus;
import ru.yandex.money.ymtransfer.R;
import ru.yandex.money.ymtransfer.events.AuthorizationEvent;
import ru.yandex.money.ymtransfer.events.ReceiveTokenEvent;

@EActivity(R.layout.activity_auth)
public class AuthActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "B06CB2A09433850862BF5B13BFD474BAFC69DEDEA4244FC41998055B7C3264F5";
    public static final String REDIRECT_URL = "https://money.yandex.ru";
    public static final String REDIRECT_URL_MOBILE = "https://m.money.yandex.ru";

    public static final String EXTRA_TOKEN = "token";

    private DefaultApiClient defaultApiClient = new DefaultApiClient(CLIENT_ID);
    private AuthorizationCodeResponse response;

    @ViewById(R.id.web)
    WebView webView;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void init() {
        initToolbar();
        initAuthWeb();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initAuthWeb() {
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
    }

    private class AuthWebViewClient extends WebViewClient {

        private AuthWebViewClient() {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(REDIRECT_URL)) {
                view.goBack();
                try {
                    response = AuthorizationCodeResponse.parse(url);
                    new GetTokenTask().execute(response.code != null ? response.code : "0");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return false;
            }

            webView.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new AuthorizationTask().execute();
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

    public void onEventMainThread(AuthorizationEvent event) {
        if (!event.isHandled()) {
            webView.loadUrl(event.getRedirectUri());
            if (event.isSuccess()) {
                Toast.makeText(this, R.string.auth_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.auth_success, Toast.LENGTH_SHORT).show();
            }
            event.setHandled(true);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    public void onEventMainThread(ReceiveTokenEvent event) {
        if (!event.isHandled()) {
            handleTokenEvent(event);
            event.setHandled(true);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void handleTokenEvent(ReceiveTokenEvent event) {
        Token token = event.getToken();
        if (token == null) {
            Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Intent authResult = new Intent();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.authorization);

        if (token.error == null) {
            builder.setMessage(R.string.auth_success);
        } else {
            builder.setMessage(getString(R.string.error, event.getToken().error.code));
            authResult.putExtra("error", token.error.code);
        }

        if (token.accessToken != null) {
            authResult.putExtra(EXTRA_TOKEN, token.accessToken);
            setResult(RESULT_OK, authResult);
        } else {
            setResult(RESULT_CANCELED, authResult);
        }

        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private class AuthorizationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            AuthorizationEvent event = new AuthorizationEvent();

            RequestBody requestBody = new FormEncodingBuilder()
                    .add("client_id", CLIENT_ID)
                    .add("response_type", "code")
                    .add("redirect_uri", REDIRECT_URL)
                    .add("scope", "account-info operation-history operation-details payment-p2p.limit(1,10)")
                    .build();
            Request request = new Request.Builder()
                    .url("https://m.money.yandex.ru/oauth/authorize")
                    .post(requestBody)
                    .build();
            try {
                Response response = defaultApiClient.getHttpClient().newCall(request).execute();
                event.setSuccess(true);
                event.setRedirectUri(response.headers().get("Location"));
            } catch (IOException e) {
                event.setSuccess(false);
                event.setRedirectUri(REDIRECT_URL);
                event.setMessage(e.toString());
                e.printStackTrace();
            }
            EventBus.getDefault().postSticky(event);
            return null;
        }
    }

    private class GetTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            ReceiveTokenEvent event = new ReceiveTokenEvent();

            Token token = null;
            Token.Request request = new Token.Request(params[0], CLIENT_ID, REDIRECT_URL);
            OAuth2Session session = new OAuth2Session(defaultApiClient);
            try {
                token = session.execute(request);
                event.setSuccess(true);
            } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
                event.setMessage(e.toString());
                event.setSuccess(false);
                e.printStackTrace();
            }
            event.setToken(token);

            EventBus.getDefault().postSticky(event);
            return null;
        }
    }
}
