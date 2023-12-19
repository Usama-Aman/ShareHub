package interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @GET("{file}")
    Call<ResponseBody> downloadFile(@Path("file") String file);
}
