package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ApiResponse;
import model.ResponseModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.UncheckedIOException;

public class RetrofitUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ApiResponse execute(Call<ResponseModel> call) {
        try {
            Response<ResponseModel> response = call.execute();
            ResponseModel body;
            if (response.isSuccessful()) {
                body = response.body();
            } else {
                body = parseErrorBody(response.errorBody());
            }
            return new ApiResponse(response.code(), body);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось выполнить запрос к приложению", e);
        }
    }

    private static ResponseModel parseErrorBody(ResponseBody errorBody) throws IOException {
        if (errorBody == null) {
            return null;
        }
        String content = errorBody.string();
        if (content.isBlank()) {
            return null;
        }
        return OBJECT_MAPPER.readValue(content, ResponseModel.class);
    }
}
