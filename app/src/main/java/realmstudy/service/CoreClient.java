package realmstudy.service;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by developer on 8/31/16.
 */

public interface CoreClient {

    String owner="dGF4aV9hbGw=/";

    //    @GET("{owner}")
//    Call<String> coreDetails(@Path(value = "owner",encoded = true) String owner);
//    @GET("{owner}")
//    Call<ResponseBody> coreDetails(@Path(value = "owner", encoded = true) String owner, @Query("type") String url, @Query(value = "encode", encoded = true) String auth_key);

    @GET("dGF4aV9hbGw/?type=getcoreconfig")
   Call<ResponseBody> coreDetails();
}
