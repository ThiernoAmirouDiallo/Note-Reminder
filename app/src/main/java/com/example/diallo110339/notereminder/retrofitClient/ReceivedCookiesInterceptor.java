package com.example.diallo110339.notereminder.retrofitClient;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;
/**
 * Sauvegarde des cookies se trouvant dans les reponse http du serveur dans le preferences
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            Methods.setCookies(MyApplication.getAppContext(), cookies);
        }
        return originalResponse;
    }
}