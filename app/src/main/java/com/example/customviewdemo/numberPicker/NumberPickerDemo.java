package com.example.customviewdemo.numberPicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;
import com.example.customviewdemo.numberPicker.NumberPicker;
import com.example.customviewdemo.utils.SoundPoolUtils;


public class NumberPickerDemo extends BaseActivity {

    private NumberPicker   numberPicker;
    private SoundPoolUtils soundPoolUtils;
    private int            loadId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numberpick_activity_main);

        getActionBar().setTitle("数字滚轮-WheelView");

        soundPoolUtils = SoundPoolUtils.getInstance(this);
        loadId = soundPoolUtils.loadVideo(R.raw.numberpicker_sound_effect);

        numberPicker = findViewById(R.id.numberpicker);
        final String[] arr = new String[20];
        for (int i = 0; i < 5; i++) {
            arr[i] = i+"";
        }

        for (int i = 10; i < 15; i++) {
            arr[i-5] = i+"";
        }

        for (int i = 20; i < 25; i++) {
            arr[i-10] = i+"";
        }

        for (int i = 30; i < 35; i++) {
            arr[i-15] = i+"";
        }
//        for (int i = 0; i < 20; i++) {
//            arr[i] = "" + i;
//        }
        numberPicker.setDisplayedValues(arr);
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(19);
        numberPicker.setValue(7);


        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker ssNumberPickerView, int i, int i1) {
                Log.d("huruidong", "at ssui at com ---> onValueChange() key: " + numberPicker.getValue());
            }
        });


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPicker.setValue((numberPicker.getValue() + 1)% numberPicker.getMaxValue());
            }
        });
    }

}
