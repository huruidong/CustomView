package com.example.customviewdemo.checkbox;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class CheckboxDemo extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkbox_activity_main);
        getActionBar().setTitle("多选框-Checkbox");
    }

}
