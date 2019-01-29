package com.bytedance.android.lesson.restapi.solution;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytedance.android.lesson.restapi.solution.bean.Cat;
import com.bytedance.android.lesson.restapi.solution.newtork.ICatService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.ViewHolder;

public class Solution2C1Activity extends AppCompatActivity {

    private static final String TAG = Solution2C1Activity.class.getSimpleName();
    public Button mBtn;
    public RecyclerView mRv;
    private List<Cat> mCats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution2_c1);
        mBtn = findViewById(R.id.btn);
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new Adapter() {
            @NonNull @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;
                Glide.with(iv.getContext()).load(mCats.get(i).getUrl()).into(iv);
            }

            @Override public int getItemCount() {
                return mCats.size();
            }
        });
    }

    public static class MyViewHolder extends ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void requestData(View view) {
        mBtn.setText(R.string.requesting);
        mBtn.setEnabled(false);
        RetrofitManager.get(ICatService.HOST).create(ICatService.class).randomCat(5).enqueue(new Callback<List<Cat>>() {
            @Override public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response.body() + "]");
                restoreBtn();
                loadPics(response.body());
            }

            @Override public void onFailure(Call<List<Cat>> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                restoreBtn();
            }
        });
    }

    private void loadPics(List<Cat> cats) {
        mCats = cats;
        mRv.getAdapter().notifyDataSetChanged();
    }

    private void restoreBtn() {
        mBtn.setText(R.string.request_data);
        mBtn.setEnabled(true);
    }
}
