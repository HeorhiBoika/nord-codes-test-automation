package api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

@Slf4j
public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        log.info("APP --> {} {} headers=[Accept={}, X-Api-Key={}] body=[{}]",
                request.method(),
                request.url(),
                request.header("Accept"),
                request.header("X-Api-Key"),
                bodyToString(request.body()));

        long start = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long durationMs = System.currentTimeMillis() - start;

        log.info("APP <-- {} ({} ms) body={}",
                response.code(),
                durationMs,
                response.peekBody(Long.MAX_VALUE).string());
        return response;
    }

    private static String bodyToString(RequestBody body) throws IOException {
        if (body == null) {
            return "";
        }
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }
}
