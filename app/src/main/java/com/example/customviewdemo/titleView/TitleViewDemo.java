package com.example.customviewdemo.titleView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.customviewdemo.R;

public class TitleViewDemo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_view_activity);
    }
}
