/**
 * MIT License
 * <p>
 * Copyright (c) 2016 yanbo
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.example.customviewdemo.slideLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.example.customviewdemo.R;

import java.util.logging.LogRecord;

/**
 * like SlidingPaneLayout, all direction support.
 */
public class SlideLayout extends ViewGroup {
    public static final int STATE_CLOSE   = 0;
    public static final int STATE_SLIDING = 1;
    public static final int STATE_OPEN    = 2;

    private static final int SLIDE_HORIZONTAL = 1;
    private static final int SLIDE_VERTICAL   = 2;

    private View mContentView;
    private View mSlideView;

    private int mLastX = 0;
    private int mLastY = 0;

    private int     mSlideCriticalValue = 0;
    private boolean mIsScrolling        = false;
    private int     mSlideDirection;
    private int     isPreOpenAction     = 0;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        mSlideDirection = typedArray.getInt(R.styleable.SlideLayout_slideDirection, SLIDE_HORIZONTAL);
        mSlideCriticalValue = typedArray.getDimensionPixelSize(
                R.styleable.SlideLayout_slideCriticalValue, 0);
        typedArray.recycle();
    }

    public int getSlideState() {
        int retValue = STATE_CLOSE;
        if (mIsScrolling) {
            retValue = STATE_SLIDING;
        } else {
            float scrollOffset = (mSlideDirection == SLIDE_HORIZONTAL) ? mContentView.getTranslationX() : mContentView.getTranslationY();
            retValue = (scrollOffset == 0) ? STATE_CLOSE : STATE_OPEN;
        }
        return retValue;
    }

    public void smoothCloseSlide() {
        switch (mSlideDirection) {
            case SLIDE_HORIZONTAL:
                mContentView.setTranslationX(0);
                break;
            case SLIDE_VERTICAL:
                mContentView.setTranslationY(0);
                break;
        }
    }

    public void smoothOpenSlide() {
        switch (mSlideDirection) {
            case SLIDE_HORIZONTAL:
                mContentView.setTranslationX(-mSlideView.getMeasuredHeight());
                break;
            case SLIDE_VERTICAL:
                mContentView.setTranslationY(mSlideView.getMeasuredHeight());
                break;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException(
                    "SlideLayout only need contains two child (content and slide).");
        }

        mContentView = getChildAt(1);
        mSlideView = getChildAt(0);
//        mContentView.setTranslationX(-100);
//        mSlideView.setTranslationX(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mSlideDirection) {
            case SLIDE_HORIZONTAL:
                mSlideView.layout(getMeasuredWidth() - mSlideView.getMeasuredWidth(), 0,
                                  getMeasuredWidth(), getMeasuredHeight());
                break;
            case SLIDE_VERTICAL:
                mSlideView.layout(0, 0, getMeasuredWidth(), mSlideView.getMeasuredHeight());
                break;
        }
        mContentView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mIsScrolling || super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventX  = (int) event.getX();
        int eventY  = (int) event.getY();
        int offsetX = eventX - mLastX;
        int offsetY = eventY - mLastY;
        float scrollX = mContentView.getTranslationX();
        float scrollY = mContentView.getTranslationX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = eventX;
                mLastY = eventY;
                mIsScrolling = false;
                //Maybe child not set OnClickListener, so ACTION_DOWN need to return true and use super.
                super.dispatchTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                int directionMoveOffset = 0;
                if (mSlideDirection == SLIDE_HORIZONTAL) {
                    directionMoveOffset = Math.abs(offsetX) - Math.abs(offsetY);
                    if (offsetX > 0) {
                        isPreOpenAction = -1;
                    } else if (offsetX < 0) {
                        isPreOpenAction = 1;
                    }
                } else {
                    directionMoveOffset = Math.abs(offsetY) - Math.abs(offsetX);
                    if (offsetY > 0) {
                        isPreOpenAction = 1;
                    } else if (offsetY < 0) {
                        isPreOpenAction = -1;
                    }
                }
                if (!mIsScrolling && directionMoveOffset < ViewConfiguration.getTouchSlop()) {
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                mIsScrolling = true;
                float newScrollX = 0;
                float newScrollY = 0;
                switch (mSlideDirection) {
                    case SLIDE_HORIZONTAL:
                        newScrollX = scrollX + offsetX;
                        if (scrollX < 0) {
                            if (isPreOpenAction > 0) {
                                if (Math.abs(newScrollX) > mSlideView.getMeasuredWidth() - 1) {
                                    newScrollX = -mSlideView.getMeasuredWidth();
                                }
                            } else if (isPreOpenAction < 0) {
                                if (newScrollX > 0) {
                                    newScrollX = 0;
                                }
                            }
                        } else if (scrollX > 0) {
                            if (isPreOpenAction < 0) {
                                if (newScrollX < 0) {
                                    newScrollX = 0;
                                }
                            }
                        }
                        break;
                    case SLIDE_VERTICAL:
                        newScrollY = scrollY + offsetY;
                        if (Math.abs(newScrollY) > mSlideView.getMeasuredHeight()-1) {
                            newScrollY = mSlideView.getMeasuredHeight();
                        }
                        break;
                }
                Log.d("huruidong", "at ssui at com ---> dispatchTouchEvent() newScrollX: " + newScrollX);
                mContentView.setTranslationX(newScrollX);
                mContentView.setTranslationY(newScrollY);
//                scrollTo(newScrollX, newScrollY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsScrolling = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                int finalScrollX = 0;
                int finalScrollY = 0;
                switch (mSlideDirection) {
                    case SLIDE_HORIZONTAL:
                        if (scrollX < 0) {
                            if (isPreOpenAction < 0) {
                                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                                if (Math.abs(scrollX) <= mSlideView.getMeasuredWidth() - getSlideCriticalValue()) {
                                    finalScrollX = 0;
                                } else {
                                    finalScrollX = -mSlideView.getMeasuredWidth();
                                }
                            } else if (isPreOpenAction > 0) {
                                // 向左滑
                                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                                if (Math.abs(scrollX) > getSlideCriticalValue()) {
                                    finalScrollX = -mSlideView.getMeasuredWidth();
                                } else {
                                    finalScrollX = 0;
                                }
                            }
                        } else if (scrollX > 0) {
                            Log.d("huruidong",
                                  "at ssui at com ---> dispatchTouchEvent() scrollX: " + scrollX
                                          + "  isPreOpenAction: " + isPreOpenAction
                                          + "  getSlideCriticalValue: " + getSlideCriticalValue());
                            if (isPreOpenAction > 0) {
                                finalScrollX = 0;
                            } else if (isPreOpenAction < 0) {
                                if (scrollX < getSlideCriticalValue()) {
                                    finalScrollX = 0;
                                } else {
                                    finalScrollX = getMeasuredWidth();
                                }
                            }
                        }
                        break;
                    case SLIDE_VERTICAL:
                        if (isPreOpenAction < 0) {
                            if (Math.abs(scrollY) <= mSlideView.getMeasuredHeight() - getSlideCriticalValue()) {
                                finalScrollY = 0;
                            } else {
                                finalScrollY = mSlideView.getMeasuredHeight();
                            }
                        } else if (isPreOpenAction > 0) {
                            if (Math.abs(scrollY) > getSlideCriticalValue()) {
                                finalScrollY = mSlideView.getMeasuredHeight();
                            } else {
                                finalScrollY = 0;
                            }
                        }
                        break;
                }
                isPreOpenAction = 0;
                mContentView.setTranslationX(finalScrollX);
                mContentView.setTranslationY(finalScrollY);
                break;
        }
        mLastX = eventX;
        mLastY = eventY;
        return super.dispatchTouchEvent(event);
    }

    //TODO  when mSlideCriticalValue != 0, slide critical need fix.
    private int getSlideCriticalValue() {
        if (mSlideDirection == SLIDE_HORIZONTAL) {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = mSlideView.getMeasuredWidth() / 10;
            }
        } else {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = mSlideView.getMeasuredHeight() / 10;
            }
        }
        return mSlideCriticalValue;
    }

}
