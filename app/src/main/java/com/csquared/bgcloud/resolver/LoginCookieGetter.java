package com.csquared.bgcloud.resolver;

import android.util.Log;

import com.csquared.bgcloud.statics.Session;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginCookieGetter {
    private static final String TAG = "COOKIE";

    private String redirectUrl;
    private OkHttpClient client;

    private long onTaskStartMillis;

    private String property;

    public LoginCookieGetter(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public void get() {
        onTaskStartMillis = System.currentTimeMillis();
        enqueueRedirect();
    }

    private void enqueueRedirect() {
        Request requestForRedirect = new Request.Builder()
                .url(redirectUrl).build();
        client.newCall(requestForRedirect).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                property = response.header("Location")
                        .replace("http://kczystudy.edufe.com.cn/lms-study/login?property=", "");
                enqueueCookie();
            }
        });
    }

    private void enqueueCookie() {
        Request requestForCookie = new Request.Builder()
                .url("http://kczystudy.edufe.com.cn/lms-study/login?property=" + property).build();
        client.newCall(requestForCookie).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String cookie = response.header("Set-Cookie");
                Session.cookie = cookie;
                long time = System.currentTimeMillis() - onTaskStartMillis;
                Log.d(TAG, "Request for " + time + " ms, Cookie = " + cookie);
            }
        });
    }

}
