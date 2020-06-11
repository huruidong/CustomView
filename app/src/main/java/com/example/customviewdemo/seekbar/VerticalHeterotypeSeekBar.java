package com.example.customviewdemo.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.example.customviewdemo.R;
import com.example.customviewdemo.utils.DisplayUtils;

public class VerticalHeterotypeSeekBar extends SeekBar {

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    float curProgress = 0;

    private int convertProgress = 0;

    private float maxProgress = -1f, minProgress = -1f;

    private Drawable icon; // icon

    public VerticalHeterotypeSeekBar(Context context) {
        this(context, null);
    }

    public VerticalHeterotypeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public VerticalHeterotypeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VerticalHeterotypeSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        convertProgress = progress;
        if (minProgress == -1f || maxProgress == -1f) {
            int height        = getHeight();
            int invalidHeight = null == getThumb() ? DisplayUtils.dp2px(getContext(),
                                                                        50) : getThumb().getIntrinsicHeight();
            maxProgress = (float) Math.floor((1f - invalidHeight / 2f / height) * getMax());
            minProgress = (float) Math.ceil(invalidHeight / 2f / height * getMax());
        }
        curProgress = convertProgress * 1f / 100 * (maxProgress - minProgress) + minProgress;
        super.setProgress((int) curProgress, animate);
    }

    @Override
    public synchronized void setProgress(int progress) {
        convertProgress = progress;
        int height        = getMeasuredHeight();
        int invalidHeight = DisplayUtils.dp2px(getContext(), 50);
        if (minProgress == -1f || maxProgress == -1f) {
            maxProgress = (float) Math.floor((1f - invalidHeight / 2f / height) * getMax());
            minProgress = (float) Math.ceil(invalidHeight / 2f / height * getMax());
        }
        Log.d("huruidong",
              "at ssui at com ---> setProgress() " + "  maxProgress: " + maxProgress + "  minProgress: " + minProgress + "  result: " + curProgress + "  height: " + height + "  invalidHeight: " + invalidHeight);
        curProgress = convertProgress * 1f / 100 * (maxProgress - minProgress) + minProgress;
        super.setProgress(progress);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
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
        if (null == icon) {
            icon = getResources().getDrawable(R.drawable.hrd_radio_selected);
        }
        if (null != icon) {
            if (null != icon) {
                int  left         = (getWidth() - icon.getIntrinsicWidth()) / 2 + getPaddingTop();
                int  right        = left + icon.getIntrinsicWidth();
                int  bottom       = getHeight() - getPaddingStart() - (getThumb().getIntrinsicHeight() - icon.getIntrinsicHeight()) / 2;
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

    void onProgressChanged() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, convertProgress, true);
        }
    }

    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
        if (curProgress >= maxProgress) {
            curProgress = maxProgress;
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
                curProgress = getMax() - (getMax() * event.getY() / getHeight());
                if (null != getThumb()) {
                    if (minProgress == -1f || maxProgress == -1f) {
                        int height        = getHeight();
                        int invalidHeight = getThumb().getIntrinsicHeight();
                        maxProgress = (float) Math.floor(
                                (1f - invalidHeight / 2f / height) * getMax());
                        minProgress = (float) Math.ceil(invalidHeight / 2f / height * getMax());
                    }

                    if (curProgress <= minProgress) {
                        curProgress = minProgress;
                    }
                    if (curProgress >= maxProgress) {
                        curProgress = maxProgress;
                    }
                    convertProgress = (int) ((curProgress - minProgress) * 1f / (maxProgress - minProgress) * 100);
                }
                //设置进度
                setProgress((int) Math.ceil(curProgress) + 1);
                //每次拖动SeekBar都会调用
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                onProgressChanged();
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
