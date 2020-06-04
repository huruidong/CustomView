package com.example.customviewdemo.button;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;


public class ButtonDemo extends BaseActivity {

    HrdButton button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_activity_main);
        getActionBar().setTitle("按钮-Button");

        button = findViewById(R.id.button);

    }

}
