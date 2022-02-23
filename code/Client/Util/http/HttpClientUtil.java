package Util.http;

import configuration.HttpConfig;
import okhttp3.*;

public class HttpClientUtil {


    public static Response runSync(String finalUrl) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);
        try {
            return call.execute();
        } catch (Exception ignored) {

        }
        return null;
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpConfig.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void shutdown() {
        HttpConfig.HTTP_CLIENT.dispatcher().executorService().shutdown();
        HttpConfig.HTTP_CLIENT.connectionPool().evictAll();
    }
}
