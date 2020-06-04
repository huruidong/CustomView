package com.example.customviewdemo.titleView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class TitleViewDemo extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_view_activity);
    }
}
