package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private ListView attchListView;

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

    private boolean isUseScaleScrollbar     = true;
    private boolean isNeedRefreshScaleTrack = true;

    private int   trackHeight;
    private float trackLeft, trackbarTop, trackRight, trackBottom;
    private float scrollbarScale = SCROLLBAR_SCALE_DEFAULT;

    public ScaleScrollbar(@NonNull Context context) {
        this(context, null);
    }

    public ScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        thumbScalePaint = new Paint();
        thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbScalePaint.setAntiAlias(true);

    }

    public void attachToRecyclerView(RecyclerView attchView) {
        if (null != attchListView) {
            throw new IllegalArgumentException(
                    "ScaleScrollbar can only have one attchView");
        }
        this.attchRecyclerView = attchView;
        initScaleTrack();
        invalidate();
    }

    public void attachToListView(ListView attchView) {
        if (null != attchRecyclerView) {
            throw new IllegalArgumentException(
                    "ScaleScrollbar can only have one attchView");
        }
        this.attchListView = attchView;
        initScaleTrack();
        invalidate();
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
        if (null == attchListView && null == attchRecyclerView) {
            throw new IllegalArgumentException(
                    "ScaleScrollbar must have one attchView");
        }
        View attchView = null == attchListView ? attchRecyclerView : attchListView;
        int attchViewHeight = unDisplayViewSize(attchView)[1];
        trackHeight = (int) (attchViewHeight * scrollbarScale);
        if (scrollbarPosition == SCROLLBAR_POSITION_TOP) {
            trackbarTop = attchView.getPaddingTop() + mScaleScrollbarDefaultPaddingTop;
        } else {
            trackbarTop = (attchView.getPaddingTop() + attchViewHeight - trackHeight) / 2 + mScaleScrollbarDefaultPaddingTop;
        }
        trackBottom = trackbarTop + trackHeight;

        if (isLayoutRtl()) {
            trackLeft = attchView.getPaddingStart() + mScaleScrollbarDefaultPaddingEnd;
        } else {
            trackLeft = (attchView.getWidth() - attchView.getPaddingEnd() - mScaleScrollbarTrackWidth - mScaleScrollbarDefaultPaddingEnd);
        }
        trackRight = trackLeft + mScaleScrollbarTrackWidth;
        Log.d("huruidong", "at ssui at com ---> initScaleTrack() "
                + "\ngetMeasuredHeight: " + attchViewHeight
                + "\ngetMeasuredHeight: " + attchView.getMeasuredHeight()
                + "\ngetMeasuredHeight: " + attchView.getHeight()
                + "\ntrackHeight: " + trackHeight
                + "\ntrackbarTop: " + trackbarTop
                + "\ntrackBottom: " + trackBottom
                + "\ntrackLeft: " + trackLeft
                + "\ntrackRight: " + trackRight
        );

        isNeedRefreshScaleTrack = false;
    }

    public int[] unDisplayViewSize(View view) {
        int size[] = new int[2];
        int width = View.MeasureSpec.makeMeasureSpec(0,
                                                     View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                                                      View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        size[0] = view.getMeasuredWidth();
        size[1] = view.getMeasuredHeight();
        return size;
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.LAYOUT_DIRECTION_RTL == this.getLayoutDirection();
        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) mScaleScrollbarTrackWidth, trackHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, (int)trackLeft, (int)trackbarTop, (int)trackRight, (int)trackBottom);
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);

        if (isUseScaleScrollbar) {
            if (isNeedRefreshScaleTrack) {
                initScaleTrack();
            }
            drawTrack(c);
            drawThumb(c);
        }
    }

    private void drawThumb(Canvas c) {

        if (null == attchListView && null == attchRecyclerView) {
            throw new IllegalArgumentException(
                    "ScaleScrollbar must have one attchView");
        }

        int range  =/* null == attchListView ? */attchRecyclerView.computeVerticalScrollRange()/* : attchListView.computeVerticalScrollRange()*/;
        int offset =/* null == attchListView ? */attchRecyclerView.computeVerticalScrollOffset()/* : attchListView.computeVerticalScrollOffset()*/;
        int extent =/* null == attchListView ? */attchRecyclerView.computeVerticalScrollExtent()/* : attchListView.computeVerticalScrollExtent()*/;

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

}
