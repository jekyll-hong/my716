package com.my716.http;

import com.my716.Settings;
import okhttp3.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpHelper {
    public static OkHttpClient createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        builder.proxy(Settings.getInstance().getProxy());

        return builder.build();
    }

    public static Request createPostRequest(String url, RequestBody body) {
        Request.Builder builder = new Request.Builder();

        builder.url(url);
        builder.post(body);

        return builder.build();
    }

    public static RequestBody createFormBody(Map<String, String> form) {
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : form.keySet()) {
            builder.add(key, form.get(key));
        }

        return builder.build();
    }

    public static Request createGetRequest(String url) {
        Request.Builder builder = new Request.Builder();

        builder.url(url);
        builder.get();

        return builder.build();
    }
}
