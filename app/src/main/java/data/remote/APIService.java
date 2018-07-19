package data.remote;

import data.model.Post;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    @POST("/RetrofitExample/insert.php")
    @FormUrlEncoded
    Call<Post> savePost(@Field("os") String os,
                        @Field("model") String model,
                        @Field("manufacturer") String manufacturer,
                        @Field("type") String type,
                        @Field("user") String user,
                        @Field("nameApps") String nameApps,
                        @Field("deviceId") long deviceId,
                        @Field("latitude") double latitude,
                        @Field("longitude") double longitude);
}