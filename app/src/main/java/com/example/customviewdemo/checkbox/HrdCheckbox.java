package com.example.customviewdemo.checkbox;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class HrdCheckbox extends CheckBox {
    public HrdCheckbox(Context context) {
        this(context, null);
    }

    public HrdCheckbox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public HrdCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HrdCheckbox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
