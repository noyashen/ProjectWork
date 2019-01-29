package com.bytedance.android.lesson.restapi.solution.newtork;


import com.bytedance.android.lesson.restapi.solution.bean.Cat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Xavier.S
 * @date 2019.01.15 16:42
 */
public interface ICatService {
    String HOST = "https://api.thecatapi.com/";
    @GET("v1/images/search") Call<List<Cat>> randomCat(@Query("limit") int limit);
}
