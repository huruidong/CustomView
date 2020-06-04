package com.example.customviewdemo.slideLayout;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class SlideLayoutDemo extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SlideLayout");
        setContentView(R.layout.slide_activity_main);
    }
}
