package network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import application.AppController;
import models.GeneralResponse;
import models.PostCommentResponseData;
import models.VideoUploadResponse;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utility.SharedPreference;
import utility.URLs;

public class ApiClientMedia {

    private static Retrofit retrofit = null;
    private ApiInterface apiInterface;
    private static ApiClientMedia apiClient;
    private Gson gson;


    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json");
                        if (SharedPreference.isUserLoggedIn(AppController.getInstance().getApplicationContext())) {
                            ongoing.addHeader("Authorization", "Bearer".concat(" ").concat(SharedPreference.getUserDetails(AppController.getInstance().getApplicationContext()).getToken()));
                        }
                        return chain.proceed(ongoing.build());
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        return client;
    }



    public ApiClientMedia() {
        this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        this.apiInterface = retrofit.create(ApiInterface.class);
    }

    public static ApiClientMedia getInstance() {
        if (apiClient == null) {
            setInstance(new ApiClientMedia());
        }
        return apiClient;
    }

    public static void setInstance(ApiClientMedia apiClient) {
        ApiClientMedia.apiClient = apiClient;
    }

    public void uploadImage(RequestBody images, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.uploadImage(images);
        call.enqueue(callback);
    }


//    public void getEventMedia(int param1, int param2, Callback<EventMediaResponse> callback) {
//        Call<EventMediaResponse> call = this.apiInterface.getEventMedia(param1, param2);
//        call.enqueue(callback);
//    }

    public void uploadMediaImage(RequestBody user_id, MultipartBody.Part images, Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.uploadMediaImage(user_id, images);
        call.enqueue(callback);
    }

    public void uploadMediaVideo(RequestBody event_id, MultipartBody.Part video, Callback<VideoUploadResponse> callback) {
        Call<VideoUploadResponse> call = null;
        call = this.apiInterface.uploadMediaVideo(event_id, video);
        call.enqueue(callback);
    }

    public void deleteMedia(JsonObject media,
                            Callback<GeneralResponse> callback) {
        Call<GeneralResponse> call = null;
        call = this.apiInterface.deleteMedia(media);
        call.enqueue(callback);
    }

    public void getResponseSucessComment(JsonObject params, Callback<PostCommentResponseData> callback) {
        Call<PostCommentResponseData> call = this.apiInterface.getResponsePostComment(params);
        call.enqueue(callback);
    }


}