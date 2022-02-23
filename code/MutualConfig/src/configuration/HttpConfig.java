package configuration;

import okhttp3.*;
import com.sun.istack.internal.NotNull;

import java.io.IOException;

public class HttpConfig {
    public final static String BASE_URL = "http://localhost:8080/gpup";
    public final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(new CookieManager())
                    .build();

    public static Callback SIMPLE_CALLBACK = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("Oops... something went wrong... error: " + e.getMessage());
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            System.out.println("Response:");
            System.out.println(response.body().string());
        }
    };

    public static void main(String[] args) {
        Request request = new Request.Builder().url(BASE_URL + "/hello").build();
    }
}
