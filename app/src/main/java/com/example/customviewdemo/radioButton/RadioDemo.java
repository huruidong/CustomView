package com.example.customviewdemo.radioButton;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class RadioDemo extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio_activity_main);
        getActionBar().setTitle("单选框-RadioButton");
    }

}
