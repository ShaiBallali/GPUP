package configuration;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CookieManager implements CookieJar {

    private final Map<String, Map<String, Cookie>> cookies = new HashMap<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        String host = httpUrl.host();
        List<Cookie> cookiesPerDomain = Collections.emptyList();
        synchronized (this) {
            if (cookies.containsKey(host)) {
                cookiesPerDomain = new ArrayList<>(cookies.get(host).values());
            }
        }
        return cookiesPerDomain;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> responseCookies) {
        String host = httpUrl.host();
        synchronized (this) {
            Map<String, Cookie> cookiesMap = cookies.computeIfAbsent(host, key -> new HashMap<>());
            responseCookies
                    .stream()
                    .filter(cookie -> !cookiesMap.containsKey(cookie.name()))  // I have the freedom to choose not to accept changes in existing cookie
                    .forEach(cookie -> {
                        cookiesMap.put(cookie.name(), cookie);
                    });
        }
    }
}
