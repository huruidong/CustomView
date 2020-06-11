package com.example.customviewdemo.seekbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;
import com.example.customviewdemo.utils.DisplayUtils;

public class SeekbarDemo extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seekbar_activity_main);
        getActionBar().setTitle("滑动条-Seekbar");
    }
}
