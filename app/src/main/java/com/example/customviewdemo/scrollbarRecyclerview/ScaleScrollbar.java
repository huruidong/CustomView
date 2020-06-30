package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.customviewdemo.R;

public class ScaleScrollbar extends View {

    private RecyclerView attchRecyclerView;

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

    private Paint thumbScalePaint, trackScalePaint, thumbArcScalePaint, trackArcScalePaint;

    private boolean isUseScaleScrollbar     = true;
    private boolean isNeedRefreshScaleTrack = true;

    private int   trackHeight;
    private float trackLeft, trackbarTop, trackRight, trackBottom;
    private float scrollbarScale = SCROLLBAR_SCALE_DEFAULT;

    private boolean isArc = false;
    private float trackArcRadius;
    private int   trackStartAngle, trackSweepAngle;
    private float trackArcLeft, trackArcTop, trackArcRight, trackArcBottom;

    private int screenWidth, screenHeight;

    public ScaleScrollbar(@NonNull Context context) {
        this(context, null);
    }

    public ScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        initAttr(context, attrs);
        if (isUseScaleScrollbar) {
            initScaleScrollbarPaint();
        }
    }

    private void initAttr(Context context, AttributeSet attrs) {
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
        setArc(a.getBoolean(R.styleable.HrdRecyclerview_scaleScrollbarArc, false));
        setUseScaleScrollbar(a.getBoolean(R.styleable.HrdRecyclerview_needScaleScrollbar, true));
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

        trackArcScalePaint = new Paint();
        trackArcScalePaint.setAntiAlias(true);
        trackArcScalePaint.setColor(mScaleScrollbarTrackColor);
        trackArcScalePaint.setStyle(Paint.Style.STROKE);
        trackArcScalePaint.setStrokeWidth(mScaleScrollbarTrackWidth);
        trackArcScalePaint.setStrokeCap(Paint.Cap.ROUND);

        thumbScalePaint = new Paint();
        thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbScalePaint.setAntiAlias(true);

        thumbArcScalePaint = new Paint();
        thumbArcScalePaint.setAntiAlias(true);
        thumbArcScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbArcScalePaint.setStyle(Paint.Style.STROKE);
        thumbArcScalePaint.setStrokeWidth(mScaleScrollbarThumbWidth);
        thumbArcScalePaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void attachToRecyclerView(RecyclerView attchView) {
        this.attchRecyclerView = attchView;
        attchRecyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                invalidate();
            }
        });
        initScaleTrack();
        invalidate();
    }

    public void setArc(boolean arc) {
        isArc = arc;
        isNeedRefreshScaleTrack = true;
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

    public void setUseScaleScrollbar(boolean useCustomScaleScrollbar) {
        isUseScaleScrollbar = useCustomScaleScrollbar;
        if (isUseScaleScrollbar) {
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

    private void initScaleTrack() {
        if (null == attchRecyclerView) {
            throw new IllegalArgumentException("ScaleScrollbar must have one attchView");
        }

        if (isArc) {
            trackArcRadius = Math.min(screenHeight, screenWidth) / 2f;
            Log.d("huruidong", "at ssui at com ---> initScaleTrack() isLayoutRtl(): " + isLayoutRtl());
            trackStartAngle = isLayoutRtl() ? ((int) (180 * (1 - scrollbarScale) / 2) + 90) : ((int) (180 * (1 - scrollbarScale) / 2) - 90);
            trackSweepAngle = (int) (180 * scrollbarScale);

            trackArcLeft = isLayoutRtl() ?(0f+mScaleScrollbarDefaultPaddingEnd): (0f - mScaleScrollbarDefaultPaddingEnd);
            trackArcTop = 0f + mScaleScrollbarDefaultPaddingTop;
            trackArcRight = trackArcLeft + trackArcRadius * 2f;
            trackArcBottom = trackArcTop + trackArcRadius * 2f;
        } else {
            View attchView       = attchRecyclerView;
            trackHeight = (int) (screenHeight * scrollbarScale);
            if (scrollbarPosition == SCROLLBAR_POSITION_TOP) {
                trackbarTop = attchView.getPaddingTop() + mScaleScrollbarDefaultPaddingTop;
            } else {
                trackbarTop = (screenHeight - trackHeight) / 2 + mScaleScrollbarDefaultPaddingTop;
            }
            trackBottom = trackbarTop + trackHeight;

            if (isLayoutRtl()) {
                trackLeft = attchView.getPaddingStart() + mScaleScrollbarDefaultPaddingEnd;
            } else {
                trackLeft = (screenWidth - attchView.getPaddingEnd() - mScaleScrollbarTrackWidth - mScaleScrollbarDefaultPaddingEnd);
            }
            trackRight = trackLeft + mScaleScrollbarTrackWidth;
        }
        isNeedRefreshScaleTrack = false;
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.LAYOUT_DIRECTION_RTL == getResources().getConfiguration().getLayoutDirection();
        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenHeight);
    }


    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(0, 0, screenWidth, screenHeight);
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        if (isUseScaleScrollbar) {
            if (isNeedRefreshScaleTrack) {
                initScaleTrack();
            }
            if (isArc) {
                drawArcTrack(c);
                drawArcThumb(c);
            } else {
                drawTrack(c);
                drawThumb(c);
            }
        }
    }

    private void drawThumb(Canvas c) {

        if (null == attchRecyclerView) {
            throw new IllegalArgumentException("ScaleScrollbar must have one attchView");
        }

        int range  = attchRecyclerView.computeVerticalScrollRange();
        int offset = attchRecyclerView.computeVerticalScrollOffset();
        int extent = attchRecyclerView.computeVerticalScrollExtent();

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
    }

    private void drawArcThumb(Canvas c) {

        if (null == attchRecyclerView) {
            throw new IllegalArgumentException("ScaleScrollbar must have one attchView");
        }

        int range  = attchRecyclerView.computeVerticalScrollRange();
        int offset = attchRecyclerView.computeVerticalScrollOffset();
        int extent = attchRecyclerView.computeVerticalScrollExtent();

        float thumbArcLeft   = trackArcLeft;
        float thumbArcTop    = trackArcTop;
        float thumbArcRight  = trackArcRight;
        float thumbArcBottom = trackArcBottom;

        int thumbSweepAngle = (int) ((extent * 1f / range) * trackSweepAngle);
        int thumbStartAngle = isLayoutRtl() ?  ((int) (trackStartAngle + (trackSweepAngle - thumbSweepAngle) * 1f * (1f - (offset * 1f / (range - extent)))))
                :((int) (trackStartAngle + (trackSweepAngle - thumbSweepAngle) * 1f * (offset * 1f / (range - extent))));
        c.drawArc(thumbArcLeft, thumbArcTop, thumbArcRight, thumbArcBottom, thumbStartAngle,
                  thumbSweepAngle, false, thumbArcScalePaint);
    }

    private void drawArcTrack(Canvas c) {
        c.drawArc(trackArcLeft, trackArcTop, trackArcRight, trackArcBottom, trackStartAngle,
                  trackSweepAngle, false, trackArcScalePaint);

    }

}
