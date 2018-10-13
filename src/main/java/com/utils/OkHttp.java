package com.utils;

import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class OkHttp {
    private static OkHttpClient sClient = null;

    /**
     * GET
     */
    public static InputStream get(String url, Map<String, String> properties) {
        InputStream input = null;

        try {
            Request request = createGetRequest(url, properties);

            Response response = getClient().newCall(request).execute();
            if (response.isSuccessful()) {
                input = response.body().byteStream();
            }
            else {
                /**
                 * 访问失败
                 */
                response.close();
            }
        }
        catch (IOException e) {
            /**
             * 网络异常
             */
        }

        return input;
    }

    /**
     * 获取Client
     */
    private static OkHttpClient getClient() {
        if (sClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            /**
             * 通用设置
             */
            builder.followRedirects(true);

            sClient = builder.build();
        }

        return sClient;
    }

    /**
     * 创建GET请求
     */
    private static Request createGetRequest(String url, Map<String, String> property) {
        Request.Builder builder = new Request.Builder();

        builder.url(url);
        builder.get();

        if (property != null && !property.isEmpty()) {
            for (Map.Entry<String, String> entry : property.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }

    /**
     * 构造函数（私有属性，不允许创建实例）
     */
    private OkHttp() {
        /**
         * nothing
         */
    }
}
