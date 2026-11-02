package api;

import model.ResponseModel;
import retrofit2.Call;
import retrofit2.http.*;

import static constants.ProjectConstants.ENDPOINT_PATH;

public interface EndpointApi {

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST(ENDPOINT_PATH)
    Call<ResponseModel> endpoint(@Header("X-Api-Key") String apiKey,
                                 @Field("token") String token,
                                 @Field("action") String action);
}
