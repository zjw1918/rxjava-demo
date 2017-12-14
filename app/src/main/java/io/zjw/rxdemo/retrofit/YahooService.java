package io.zjw.rxdemo.retrofit;

import io.reactivex.Single;
import io.zjw.rxdemo.gson.YahooStockResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mega on 2017/12/14.
 */

public interface YahooService {
    @GET("yql?format=json")
    Single<YahooStockResult> yqlQuery(@Query("q") String query, @Query("env") String env);
}
