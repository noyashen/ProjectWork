package com.bytedance.android.lesson.restapi.solution.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Xavier.S
 * @date 2019.01.20 14:18
 */
public class Feed {
    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("videoUrl")
    public String videoUrl;

    @SerializedName("studentId")
    public String studentId;

    @SerializedName("userName")
    public String userName;
}
