package api;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.Duration;

@Slf4j
public class RetrofitClient {

    private static RetrofitClient instance;
    private final OkHttpClient httpClient;

    private RetrofitClient() {
        this.httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(new AllureOkHttp3())
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public <T> T getRetrofitObject(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }
}
