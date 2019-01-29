package com.bytedance.android.lesson.restapi.solution;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.btn_s1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Solution2C2(v);
            }
        });

    }
    public void Solution2C2(View view) {
        startActivity(new Intent(this, Solution2C2Activity.class));

    }
}
