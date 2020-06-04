package com.example.customviewdemo.radioButton;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class HrdRadioButton extends RadioButton {
    public HrdRadioButton(Context context) {
        this(context, null);
    }

    public HrdRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public HrdRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HrdRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
