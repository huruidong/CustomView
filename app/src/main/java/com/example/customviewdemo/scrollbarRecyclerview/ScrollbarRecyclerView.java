package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.customviewdemo.R;

public class ScrollbarRecyclerView extends RecyclerView {

    private static final int SCROLLBAR_POSITION_TOP    = 1;
    private static final int SCROLLBAR_POSITION_MIDDLE = 2;

    private static final float SCROLLBAR_SCALE_DEFAULT = 0.3F;

    private int scrollbarPosition = SCROLLBAR_POSITION_MIDDLE;

    private float mScaleScrollbarTrackWidth        = getResources().getDimension(
            R.dimen.hrd_scale_scrollbar_track_width_default);
    private float mScaleScrollbarThumbWidth        = getResources().getDimension(
            R.dimen.hrd_scale_scrollbar_thumb_width_default);
    private float mScaleScrollbarDefaultPaddingTop = getResources().getDimension(
            R.dimen.hrd_scale_scrollbar_paddingTop_default);
    private float mScaleScrollbarDefaultPaddingEnd = getResources().getDimension(
            R.dimen.hrd_scale_scrollbar_paddingEnd_default);

    private int mScaleScrollbarTrackColor = getResources().getColor(
            R.color.hrd_scale_scrollbar_track_color);
    private int mScaleScrollbarThumbColor = getResources().getColor(
            R.color.hrd_scale_scrollbar_thumb_color);

    private Paint thumbScalePaint, trackScalePaint;

    private boolean isNeedScaleScrollbar    = true;
    private boolean isNeedRefreshScaleTrack = true;

    private int   trackHeight;
    private float trackLeft, trackbarTop, trackRight, trackBottom;
    private float scrollbarScale = SCROLLBAR_SCALE_DEFAULT;

    public ScrollbarRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollbarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScaleScrollbarAttr(context, attrs);
        if (isNeedScaleScrollbar) {
            initScaleScrollbarPaint();
        }
    }

    private void initScaleScrollbarAttr(Context context, AttributeSet attrs) {
        if (null == context || null == attrs) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HrdRecyclerview);

        setScaleScrollbarTrackColor(a.getColor(R.styleable.HrdRecyclerview_scaleScrollbarTrackColor,
                                               context.getColor(
                                                       R.color.hrd_scale_scrollbar_track_color)));
        setScaleScrollbarThumbColor(a.getColor(R.styleable.HrdRecyclerview_scaleScrollbarThumbColor,
                                               context.getColor(
                                                       R.color.hrd_scale_scrollbar_thumb_color)));
        setScrollbarScale(
                a.getFloat(R.styleable.HrdRecyclerview_scrollbarScale, SCROLLBAR_SCALE_DEFAULT));
        setNeedScaleScrollbar(a.getBoolean(R.styleable.HrdRecyclerview_needScaleScrollbar, true));
        setScaleScrollbarTrackWidth(
                a.getDimension(R.styleable.HrdRecyclerview_scaleScrollbarTrackWidth,
                               getResources().getDimension(
                                       R.dimen.hrd_scale_scrollbar_track_width_default)));
        setScaleScrollbarThumbWidth(
                a.getDimension(R.styleable.HrdRecyclerview_scaleScrollbarThumbWidth,
                               getResources().getDimension(
                                       R.dimen.hrd_scale_scrollbar_thumb_width_default)));
        setScaleScrollbarPadding(
                a.getDimension(R.styleable.HrdRecyclerview_scaleScrollbarPaddingTop,
                               getResources().getDimension(
                                       R.dimen.hrd_scale_scrollbar_paddingTop_default)),
                a.getDimension(R.styleable.HrdRecyclerview_scaleScrollbarPaddingEnd,
                               getResources().getDimension(
                                       R.dimen.hrd_scale_scrollbar_paddingEnd_default)));
        setScrollbarPosition(a.getInt(R.styleable.HrdRecyclerview_scaleScrollbarPosition,
                                      SCROLLBAR_POSITION_MIDDLE));
        a.recycle();
    }

    /**
     * 定义画笔样式
     **/
    private void initScaleScrollbarPaint() {

        trackScalePaint = new Paint();
        trackScalePaint.setAntiAlias(true);
        trackScalePaint.setColor(mScaleScrollbarTrackColor);

        thumbScalePaint = new Paint();
        thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbScalePaint.setAntiAlias(true);

    }

    public void setScrollbarPosition(int scrollbarPosition) {
        this.scrollbarPosition = scrollbarPosition;
    }

    public void setScaleScrollbarTrackColor(int mScaleScrollbarTrackColor) {
        this.mScaleScrollbarTrackColor = mScaleScrollbarTrackColor;
        if (null != trackScalePaint) {
            trackScalePaint.setColor(mScaleScrollbarTrackColor);
        }
    }

    public void setScaleScrollbarThumbColor(int mScaleScrollbarThumbColor) {
        this.mScaleScrollbarThumbColor = mScaleScrollbarThumbColor;
        if (null != thumbScalePaint) {
            thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        }
    }

    public void setScrollbarScale(float scrollbarScale) {
        this.scrollbarScale = scrollbarScale;
        isNeedRefreshScaleTrack = true;
    }

    public void setNeedScaleScrollbar(boolean useCustomScaleScrollbar) {
        isNeedScaleScrollbar = useCustomScaleScrollbar;
        if (isNeedScaleScrollbar) {
            initScaleScrollbarPaint();
        }
    }

    public void setScaleScrollbarTrackWidth(float mScaleScrollbarTrackWidth) {
        this.mScaleScrollbarTrackWidth = mScaleScrollbarTrackWidth;
        isNeedRefreshScaleTrack = true;
    }

    public void setScaleScrollbarThumbWidth(float mScaleScrollbarThumbWidth) {
        this.mScaleScrollbarThumbWidth = mScaleScrollbarThumbWidth;
    }

    public void setScaleScrollbarPadding(float top, float end) {
        this.mScaleScrollbarDefaultPaddingTop = top;
        this.mScaleScrollbarDefaultPaddingEnd = end;
        isNeedRefreshScaleTrack = true;
    }

    private void initScaleTrack(Canvas canvas) {

        int[] location = new  int[2] ;
        getLocationInWindow(location); //获取在当前窗口内的绝对坐标

        trackHeight = (int) (getHeight() * scrollbarScale);
        if (scrollbarPosition == SCROLLBAR_POSITION_TOP) {
            trackbarTop = location[1] + mScaleScrollbarDefaultPaddingTop;
        } else {
            trackbarTop = (location[1] + getHeight() - trackHeight) / 2 + mScaleScrollbarDefaultPaddingTop;
        }
        trackBottom = trackbarTop + trackHeight;

        if (isLayoutRtl()) {
            trackLeft = getPaddingStart() + mScaleScrollbarDefaultPaddingEnd;
        } else {
            trackLeft = (getWidth() - getPaddingEnd() - mScaleScrollbarTrackWidth - mScaleScrollbarDefaultPaddingEnd);
        }
        trackRight = trackLeft + mScaleScrollbarTrackWidth;

        isNeedRefreshScaleTrack = false;
        Log.d("huruidong", "at ssui at com ---> initScaleTrack() t: " + trackbarTop + "  l: " + trackLeft);
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.LAYOUT_DIRECTION_RTL == getResources().getConfiguration().getLayoutDirection();
        } else {
            return false;
        }
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
        if (computeVerticalScrollExtent() >= computeVerticalScrollRange()) {
            return;
        }
        if (isNeedScaleScrollbar) {
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
        float thumbLeft   = trackLeft + ((mScaleScrollbarTrackWidth - mScaleScrollbarThumbWidth) / 2);
        float thumbRight  = thumbLeft + mScaleScrollbarThumbWidth;

        c.drawRoundRect(thumbLeft, thumbTop, thumbRight, thumbBottom, mScaleScrollbarThumbWidth / 2,
                        mScaleScrollbarThumbWidth / 2, thumbScalePaint);
    }

    private void drawTrack(Canvas c) {
        c.drawRoundRect(trackLeft, trackbarTop, trackRight, trackBottom,
                        mScaleScrollbarTrackWidth / 2, mScaleScrollbarTrackWidth / 2,
                        trackScalePaint);
        Log.d("huruidong", "at ssui at com ---> drawTrack() trackbarTop: " + trackbarTop + "  trackLeft: " + trackLeft);
    }

    private float lastX = 0, lastY = 0;


//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        float curX = e.getRawX();
//        float curY = e.getRawY();
//        float offsetX = curX - lastX;
//        float offsetY = curY - lastY;
////        Log.d("huruidong", "at ssui at com ---> onTouchEvent() curY: " + curY + "  lastY: " + lastY);
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
////                Log.d("huruidong", "at ssui at com ---> onTouchEvent() key: " + "ACTION_DOWN");
//                lastX = curX;
//                lastY = curY;
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
//
////                Log.d("huruidong", "at ssui at com ---> onTouchEvent() offsetY: " + offsetY);
//                if (((offsetY > 0f || getTranslationY() != 0) && layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
//                || ((offsetY < 0 || getTranslationY() != 0) && layoutManager.findLastCompletelyVisibleItemPosition() == (getAdapter().getItemCount() -1))) {
//                    setTranslationY(offsetY);
//
////                    layoutManager.scrollToPositionWithOffset(10, (int) offsetY);
//                    return true;
//                }
//
//            case MotionEvent.ACTION_UP:
////                Log.d("huruidong", "at ssui at com ---> onTouchEvent() key: " + "ACTION_UP");
//                setTranslationY(0);
//                break;
//
//        } return super.onTouchEvent(e);
//    }


}
