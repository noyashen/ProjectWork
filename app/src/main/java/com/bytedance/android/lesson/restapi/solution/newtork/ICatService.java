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
    @GET("v1/images/search?limit=5") Call<List<Cat>> randomCat();
}
