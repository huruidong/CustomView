package com.example.customviewdemo.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class HrdSeekbar extends SeekBar {
    public HrdSeekbar(Context context) {
        super(context, null);
    }

    public HrdSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.seekBarStyle);
    }

    public HrdSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HrdSeekbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
