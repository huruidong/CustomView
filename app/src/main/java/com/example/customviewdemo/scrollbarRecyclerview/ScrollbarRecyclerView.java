package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class ScrollbarRecyclerView extends RecyclerView {

    private Paint mBgArcPaint;
    private Paint mArcPaint;
    private Paint mCirclePaint;
    private Paint mBgCirclePaint;


    private float mScrollbarTrackWidth     = 20;
    private float mScrollbarThumbWidth     = 16;
    private float mScrollbarDefaultPadding = 16;

    private int topBottomPadding = 30 + 40;

    public ScrollbarRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 定义画笔样式
     **/
    private void init() {
        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(true);
        mBgArcPaint.setColor(Color.RED);
        mBgArcPaint.setStrokeWidth(mScrollbarTrackWidth);
        mBgArcPaint.setStyle(Paint.Style.STROKE);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(Color.BLUE);
        mArcPaint.setStrokeWidth(mScrollbarTrackWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        //		setOnScrollListener(mOnScrollListener);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.YELLOW);

        mBgCirclePaint = new Paint();
        mBgCirclePaint.setAntiAlias(true);
        mBgCirclePaint.setColor(Color.GREEN);
    }

    //
    @Override
    public void draw(Canvas c) {
        super.draw(c);

        int   width           = getWidth();
        int   height          = getHeight();
        int   trackHeight = height / 3;
        float trackLeft   = (width - getPaddingEnd() - mScrollbarTrackWidth - mScrollbarDefaultPadding);
        float trackbarTop    = (height - trackHeight) / 2;
        float trackRight  = trackLeft + mScrollbarTrackWidth;
        float trackBottom = trackbarTop + trackHeight;

        Paint trackPaint = new Paint();
        trackPaint.setAntiAlias(true);
        trackPaint.setColor(Color.parseColor("#00f000"));
        c.drawRoundRect(trackLeft, trackbarTop, trackRight, trackBottom,
                        mScrollbarTrackWidth / 2, mScrollbarTrackWidth / 2, trackPaint);

        int range  = computeVerticalScrollRange();
        int offset = computeVerticalScrollOffset();
        int extent = computeVerticalScrollExtent();

        int   thumbHeight = (int) ((extent * 1f / range) * trackHeight);
        float thumbLeft   = trackLeft + ((mScrollbarTrackWidth - mScrollbarThumbWidth) / 2) -1 ;
        float thumbTop    = trackbarTop + 1 + (trackHeight-thumbHeight) * 1f * (offset * 1f / (range - extent));
        float thumbRight  = thumbLeft + mScrollbarThumbWidth;
        float thumbBottom = thumbTop + thumbHeight - 3;

        Log.d("huruidong",
              "at ssui at com ---> draw() " + "\nrange: " + range + "\noffset: " + offset + "\nextent: " + extent + "\ntrackLeft: " + trackLeft + "\ntrackbarTop: " + trackbarTop + "\ntrackRight: " + trackRight + "\ntrackBottom: " + trackBottom + "\nthumbHeight: " + thumbHeight + "\nthumbLeft: " + thumbLeft + "\nthumbTop: " + thumbTop + "\nthumbRight: " + thumbRight + "\nthumbBottom: " + thumbBottom);

        Paint thumbPaint = new Paint();
        thumbPaint.setColor(Color.parseColor("#ff0000"));
        thumbPaint.setAntiAlias(true);
        c.drawRoundRect(thumbLeft, thumbTop, thumbRight, thumbBottom, mScrollbarThumbWidth / 2,
                        mScrollbarThumbWidth / 2, thumbPaint);

    }


}
