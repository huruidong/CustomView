package com.example.customviewdemo.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.example.customviewdemo.utils.DisplayUtils;

public class HrdVerticalSeekBar extends SeekBar {

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    private int curProgress = 0;

    private Drawable icon; // icon

    public HrdVerticalSeekBar(Context context) {
        this(context, null);
    }

    public HrdVerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public HrdVerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HrdVerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public void setProgress(int progress, boolean animate) {
        super.setProgress(progress, animate);
        curProgress = progress;
        onProgressChanged(false);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        curProgress = progress;
        onProgressChanged(false);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        //将SeekBar转转90度
        c.save();
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(), 0);
        super.onDraw(c);
        c.restore();
        drawIcon(c);

    }

    private void drawIcon(Canvas c) {
        if (null != icon) {
            if (null != icon) {
                int  left         = (getWidth() - icon.getIntrinsicWidth()) / 2 + getPaddingTop();
                int  right        = left + icon.getIntrinsicWidth();
                int  bottom       = getHeight() - getPaddingStart() - getPaddingEnd() - (DisplayUtils.dp2px(
                        getContext(), 25) - icon.getIntrinsicHeight()) / 2;
                int  top          = bottom - icon.getIntrinsicHeight();
                Rect drawableRect = new Rect(left, top, right, bottom);
                icon.setBounds(drawableRect);
                icon.draw(c);
            }
        }
    }

    void onStartTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onProgressChanged(boolean fromUser) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, curProgress, fromUser);
        }
    }

    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取滑动的距离
                curProgress = (int) (getMax() - (getMax() * event.getY() / getHeight()));
                //设置进度
                super.setProgress(curProgress);
                //每次拖动SeekBar都会调用
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                onProgressChanged(true);
                break;
            case MotionEvent.ACTION_UP:
                onStopTrackingTouch();
                break;
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }
}
