package com.example.customviewdemo.titleView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.customviewdemo.R;

public class TitleView extends ViewGroup {

    private Context mContext;

    private ObjectAnimator zoomInXAnimator, zoomInYAnimator, zoomOutXAnimator, zoomOutYAnimator;

    private float mZoomScalePress = 0.9f;
    private int mZoomInDurationPress = 200;
    private int mZoomOutDurationPress = 35;

    private int mHeight; // 控件自身高度，以图片和文字中最大高度为准

    private Paint mPaint;

    private float    mTextSize; // 标题文字大小
    private int      mTextColor; // 标题文字颜色
    private String   title; // 标题内容
    private Drawable icon; // icon
    private ColorStateList iconTint = null; // icon染色
    private View     customView; // 自定义布局

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        initAttrs(context, attrs);
        setBackgroundColor(getResources().getColor(android.R.color.black));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("huruidong", "at ssui at com ---> onClick() key: " + (getContext() instanceof Activity));
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }

            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        int        n = a.getIndexCount();

        setTextColor(a.getColor(R.styleable.TitleView_textColor,
                                context.getColor(R.color.hrd_primary_color)));
        setTextSize((int) a.getDimension(R.styleable.TitleView_textSize,
                                         context.getResources().getDimension(
                                                 R.dimen.title_view_text_size_default)));
        setTitle(a.getString(R.styleable.TitleView_text));
        setIcon(a.getDrawable(R.styleable.TitleView_icon));
        setIconTint(a.getColorStateList(R.styleable.TitleView_iconTint));
        setCustomView(a.getResourceId(R.styleable.TitleView_customView, -1));
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != customView) {
            // 如果有自定义布局，则不需要绘制标题和icon
            return;
        }
        // 画标题
        /**
         * Ascent是baseline之上至字符最高处的距离
         * Descent是baseline之下至字符最低处的距离
         * drawText中  x是指绘制文字的起始点横坐标，y是指绘制文字的baseline的纵坐标
         */
        int y = (int) ((getMeasuredHeight() - (mPaint.descent() - mPaint.ascent())) / 2 - mPaint.ascent());
        int x = (getMeasuredWidth() - (int) mPaint.measureText(title)) / 2;
        canvas.drawText(title, x, y, mPaint);

        // 画标题前面icon
        if (null != icon) {
            icon.setTintList(iconTint);
            int  right        = x - dp2px(16);
            int  left         = right - icon.getIntrinsicWidth();
            int  top          = (int) ((getMeasuredHeight()) / 2 - icon.getIntrinsicHeight() / 2);
            int  bottom       = top + icon.getIntrinsicHeight();
            Rect drawableRect = new Rect(left, top, right, bottom);
            icon.setBounds(drawableRect);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            icon.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize  = MeasureSpec.getSize(widthMeasureSpec);

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (null != customView) {
                // 如果有自定义布局，则以自定义布局高度为准
                customView.measure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(withSize, customView.getMeasuredHeight());
            } else {
                // 否则以控件自身高度为准
                setMeasuredDimension(withSize, mHeight);
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("huruidong", "at ssui at com ---> onLayout() key: " + l +" " + t + " " + r + " " + b);
        if (null != customView) {
            customView.layout(l, t, r, b);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startZoomOutAnimator();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startZoomInAnimator();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void startZoomInAnimator() {
        if (null == zoomInXAnimator) {
            zoomInXAnimator = ObjectAnimator.ofFloat(this, "scaleX", getScaleX(), 1f);
            zoomInXAnimator.setDuration(mZoomInDurationPress);
            zoomInXAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setScaleX(1f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setScaleX(1f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (null == zoomInYAnimator) {
            zoomInYAnimator = ObjectAnimator.ofFloat(this, "scaleY", getScaleY(), 1f);
            zoomInYAnimator.setDuration(mZoomInDurationPress);
            zoomInYAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setScaleY(1f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setScaleY(1f);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (null != zoomOutXAnimator) {
            zoomOutXAnimator.cancel();
        }
        if (null != zoomOutYAnimator) {
            zoomOutYAnimator.cancel();
        }
        zoomInXAnimator.start();
        zoomInYAnimator.start();
    }

    private void startZoomOutAnimator() {
        if (null == zoomOutXAnimator) {
            zoomOutXAnimator = ObjectAnimator.ofFloat(this, "scaleX", getScaleX(), mZoomScalePress);
            zoomOutXAnimator.setDuration(mZoomOutDurationPress);
            zoomOutXAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setScaleX(mZoomScalePress);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setScaleX(mZoomScalePress);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (null == zoomOutYAnimator) {
            zoomOutYAnimator = ObjectAnimator.ofFloat(this, "scaleY", getScaleY(), mZoomScalePress);
            zoomOutYAnimator.setDuration(mZoomOutDurationPress);
            zoomOutYAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setScaleY(mZoomScalePress);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setScaleY(mZoomScalePress);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (null != zoomInXAnimator) {
            zoomInXAnimator.cancel();
        }
        if (null != zoomInYAnimator) {
            zoomInYAnimator.cancel();
        }
        zoomOutXAnimator.start();
        zoomOutYAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseAnimator();
    }

    private void releaseAnimator() {
        zoomInXAnimator = null;
        zoomInYAnimator = null;
        zoomOutXAnimator = null;
        zoomOutYAnimator = null;
    }

    public void setCustomView(int resId) {
        if (-1 != resId) {
            customView = LayoutInflater.from(mContext).inflate(resId, null);
            addView(customView);
            mHeight = customView.getMeasuredHeight();
        }
    }

    public void setCustomView(View view) {
        if (null != view) {
            customView = view;
            addView(customView);
            mHeight = customView.getMeasuredHeight();
        }
    }

    public void setIconTint(ColorStateList iconTint) {
        this.iconTint = iconTint;
        invalidate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
        int textHeight = (int) (mPaint.descent() - mPaint.ascent());
        mHeight = (mHeight > textHeight ? mHeight : textHeight) + getPaddingTop() + getPaddingBottom();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        mPaint.setColor(mTextColor);
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
        int iconHeight = icon.getIntrinsicHeight();
        mHeight = (mHeight > iconHeight ? mHeight : iconHeight) + getPaddingTop() + getPaddingBottom();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                                               getResources().getDisplayMetrics());
    }
}
