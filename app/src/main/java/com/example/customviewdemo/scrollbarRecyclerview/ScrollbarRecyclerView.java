package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.example.customviewdemo.R;

public class ScrollbarRecyclerView extends RecyclerView {

    private float mScaleScrollbarTrackWidth;
    private float mScaleScrollbarThumbWidth;
    private float mScaleScrollbarDefaultPadding;

    private int mScaleScrollbarTrackColor = Color.parseColor("#181818");
    private int mScaleScrollbarThumbColor = Color.parseColor("#5b5b5b");

    private Paint thumbScalePaint, trackScalePaint;

    private boolean isUseCustomScaleScrollbar = true;
    private boolean isNeedRefreshScaleTrack   = true;

    private int   trackHeight;
    private float trackLeft, trackbarTop, trackRight, trackBottom;
    private float scrollbarScale = 0.3f;

    public ScrollbarRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isUseCustomScaleScrollbar) {
            initScaleScrollbarPaint();
        }
    }

    /**
     * 定义画笔样式
     **/
    private void initScaleScrollbarPaint() {

        mScaleScrollbarTrackWidth = getResources().getDimensionPixelSize(R.dimen.dp_6);
        mScaleScrollbarThumbWidth = getResources().getDimensionPixelSize(R.dimen.dp_6);
        mScaleScrollbarDefaultPadding = getResources().getDimensionPixelSize(R.dimen.dp_10);

        trackScalePaint = new Paint();
        trackScalePaint.setAntiAlias(true);
        trackScalePaint.setColor(mScaleScrollbarTrackColor);

        thumbScalePaint = new Paint();
        thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbScalePaint.setAntiAlias(true);

    }

    public void setmScaleScrollbarTrackColor(int mScaleScrollbarTrackColor) {
        this.mScaleScrollbarTrackColor = mScaleScrollbarTrackColor;
        if (null != trackScalePaint) {
            trackScalePaint.setColor(mScaleScrollbarTrackColor);
        }
    }

    public void setmScaleScrollbarThumbColor(int mScaleScrollbarThumbColor) {
        this.mScaleScrollbarThumbColor = mScaleScrollbarThumbColor;
        if (null != thumbScalePaint) {
            thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        }
    }

    public void setScrollbarScale(float scrollbarScale) {
        this.scrollbarScale = scrollbarScale;
        isNeedRefreshScaleTrack = true;
    }

    public void setUseCustomScaleScrollbar(boolean useCustomScaleScrollbar) {
        isUseCustomScaleScrollbar = useCustomScaleScrollbar;
        if (isUseCustomScaleScrollbar) {
            initScaleScrollbarPaint();
        }
    }

    public void setmScaleScrollbarTrackWidth(float mScaleScrollbarTrackWidth) {
        this.mScaleScrollbarTrackWidth = mScaleScrollbarTrackWidth;
        isNeedRefreshScaleTrack = true;
    }

    public void setmScaleScrollbarThumbWidth(float mScaleScrollbarThumbWidth) {
        this.mScaleScrollbarThumbWidth = mScaleScrollbarThumbWidth;
    }

    public void setmScaleScrollbarDefaultPadding(float mScaleScrollbarDefaultPadding) {
        this.mScaleScrollbarDefaultPadding = mScaleScrollbarDefaultPadding;
        isNeedRefreshScaleTrack = true;
    }

    private void initScaleTrack(Canvas canvas) {

        trackHeight = (int) (getHeight() * scrollbarScale);

        trackbarTop = (getHeight() - trackHeight) / 2;
        trackBottom = trackbarTop + trackHeight;

        if (isLayoutRtl()) {
            trackLeft = getPaddingStart() + mScaleScrollbarDefaultPadding;
        } else {
            trackLeft = (getWidth() - getPaddingEnd() - mScaleScrollbarTrackWidth - mScaleScrollbarDefaultPadding);
        }
        trackRight = trackLeft + mScaleScrollbarTrackWidth;

        isNeedRefreshScaleTrack = false;
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.LAYOUT_DIRECTION_RTL == this.getLayoutDirection();
        } else {
            return false;
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        if (isUseCustomScaleScrollbar) {
            if (isNeedRefreshScaleTrack) {
                initScaleTrack(c);
            }
            drawTrack(c);
            drawThumb(c);
        }
    }

    private void drawThumb(Canvas c) {
        int range  = computeVerticalScrollRange();
        int offset = computeVerticalScrollOffset();
        int extent = computeVerticalScrollExtent();

        int   thumbHeight = (int) ((extent * 1f / range) * trackHeight);
        float thumbTop    = trackbarTop + (trackHeight - thumbHeight) * 1f * (offset * 1f / (range - extent));
        float thumbBottom = thumbTop + thumbHeight;
        float thumbLeft = trackLeft + ((mScaleScrollbarTrackWidth - mScaleScrollbarThumbWidth) / 2);
        float thumbRight = thumbLeft + mScaleScrollbarThumbWidth;

        c.drawRoundRect(thumbLeft, thumbTop, thumbRight, thumbBottom,
                        mScaleScrollbarThumbWidth / 2, mScaleScrollbarThumbWidth / 2,
                        thumbScalePaint);
    }

    private void drawTrack(Canvas c) {
        c.drawRoundRect(trackLeft, trackbarTop, trackRight, trackBottom,
                        mScaleScrollbarTrackWidth / 2, mScaleScrollbarTrackWidth / 2,
                        trackScalePaint);
    }

}
