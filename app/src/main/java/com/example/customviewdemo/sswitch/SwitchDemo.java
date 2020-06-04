package com.example.customviewdemo.sswitch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;

public class SwitchDemo extends BaseActivity {

    HrdSwitch hrdSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_activity_main);
        getActionBar().setTitle("Switch");

        hrdSwitch = findViewById(R.id.hrd_switch);
        hrdSwitch.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("huruidong",
                      "at ssui at com ---> run() " + "\nmSwitchMinWidth: " + hrdSwitch.getSwitchMinWidth() + "\nmSwitchMinWidth: " + hrdSwitch.getThumbDrawable().getIntrinsicWidth()

                );
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("huruidong",
              "at ssui at com ---> onResume() " + "\nmSwitchMinWidth: " + hrdSwitch.getSwitchMinWidth() + "\nmSwitchMinWidth: " + hrdSwitch.getThumbDrawable().getIntrinsicWidth());
    }
}
