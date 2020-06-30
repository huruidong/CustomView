package com.example.customviewdemo.slideLayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class SlideLayoutDemo extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SlideLayout");
        setContentView(R.layout.slide_activity_main);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("huruidong", "at ssui at com ---> onClick() key: " + 1);
            }
        });

        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("huruidong", "at ssui at com ---> onClick() key: " + 2);
            }
        });

        findViewById(R.id.text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("huruidong", "at ssui at com ---> onClick() key: " + 3);
            }
        });

        findViewById(R.id.text4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("huruidong", "at ssui at com ---> onClick() key: " + 4);
            }
        });
    }
}
