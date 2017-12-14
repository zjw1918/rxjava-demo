package io.zjw.rxdemo.retrofit;

import io.reactivex.Single;
import io.zjw.rxdemo.gson.RandomUserResults;
import io.zjw.rxdemo.gson.YahooStockResult;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mega on 2017/12/14.
 */

public interface RandomUserService {
    @GET("api?nat=ch")
    Single<RandomUserResults> fetch(@Query("results") int results, @Query("gender") String gender);
}
