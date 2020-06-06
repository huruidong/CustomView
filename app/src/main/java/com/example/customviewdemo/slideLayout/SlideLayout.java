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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.example.customviewdemo.R;

/**
 * like SlidingPaneLayout, all direction support.
 */
public class SlideLayout extends ViewGroup {
    public static final int STATE_CLOSE   = 0;
    public static final int STATE_SLIDING = 1;
    public static final int STATE_OPEN    = 2;

    private static final int SLIDE_HORIZONTAL = 1;
    private static final int SLIDE_VERTICAL   = 2;

    private static final int SLIDE_PRE_ACTION_DEFAULT = 0; // 无操作
    private static final int SLIDE_PRE_ACTION_OPEN    = 1; // 预测当前操作是开启操作
    private static final int SLIDE_PRE_ACTION_CLOSE   = -1; // 预测当前操作是关闭操作

    private static final long ANIM_ALPHA_DURATION_DEFAULT = 150;

    private static final float VELOCITY_LIMIT_OPEN_DEFAULT   = 1000;
    private static final float VELOCITY_LIMIT_CLOSE_DEFAULT  = 1000;
    private static final float VELOCITY_LIMIT_DELETE_DEFAULT = 2000;

    private OnSlideActionListener mListener;

    private View mContentView;
    private View mSlideView;

    private int screenWidth, screenHeight;

    private int mLastX = 0;
    private int mLastY = 0;

    private VelocityTracker mVelocityTracker;
    private float           currVelocity = 0;

    private ObjectAnimator alphaSlideViewAnimator, alphaContentViewAnimator;
    private SpringAnimation tranXSpringAnimation, tranYSpringAnimation;
    private SpringForce tranXSpringForce, tranYSpringForce;

    private long alphaAnimatorDuration = ANIM_ALPHA_DURATION_DEFAULT;

    private int     mSlideCriticalValue = 0;
    private boolean mIsScrolling        = false;
    private int     mSlideDirection;
    private int     preNextAction       = SLIDE_PRE_ACTION_DEFAULT; // 根据上一次和当前滑动位置预测下一步操作是开启还是关闭

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

    public void setmContentView(View mContentView) {
        this.mContentView = mContentView;
    }

    public void setmSlideView(View mSlideView) {
        this.mSlideView = mSlideView;
    }

    public void setmSlideCriticalValue(int mSlideCriticalValue) {
        this.mSlideCriticalValue = mSlideCriticalValue;
    }

    public void setmSlideDirection(int mSlideDirection) {
        this.mSlideDirection = mSlideDirection;
    }

    public void setOnSlideActionListener(OnSlideActionListener mListener) {
        this.mListener = mListener;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        mSlideDirection = typedArray.getInt(R.styleable.SlideLayout_slideDirection,
                                            SLIDE_HORIZONTAL);
        mSlideCriticalValue = typedArray.getDimensionPixelSize(
                R.styleable.SlideLayout_slideCriticalValue, 0);
        typedArray.recycle();
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException(
                    "SlideLayout only need contains two child (content and slide).");
        }
        mSlideView = getChildAt(0);
        mSlideView.setAlpha(0);
        mContentView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        mVelocityTracker.addMovement(event);
        int   eventX  = (int) event.getX();
        int   eventY  = (int) event.getY();
        int   offsetX = eventX - mLastX;
        int   offsetY = eventY - mLastY;
        float scrollX = mContentView.getTranslationX();
        float scrollY = mContentView.getTranslationX();
        mLastX = eventX;
        mLastY = eventY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsScrolling = false;
                super.dispatchTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mContentView.getTranslationX() >= 0 && offsetX > 0) {
                    return false;
                }
                mVelocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
                int directionMoveOffset = 0;
                if (mSlideDirection == SLIDE_HORIZONTAL) {
                    currVelocity = mVelocityTracker.getXVelocity();
                    directionMoveOffset = Math.abs(offsetX) - Math.abs(offsetY);
                    if (offsetX > 0) {
                        // 从左向右滑动时→→→
                        if (scrollX < 0) {
                            // 如果是已经打开右侧边的情况下，则为关闭
                            preNextAction = SLIDE_PRE_ACTION_CLOSE;
                        } else {
                            // 如果是默认状态或者打开左侧边的情况下，则为开启
                            preNextAction = SLIDE_PRE_ACTION_OPEN;
                        }
                    } else if (offsetX < 0) {
                        // 从右向左滑动时←←←
                        if (scrollX <= 0) {
                            // 如果是默认状态或者打开右侧边的情况下，则为打开
                            preNextAction = SLIDE_PRE_ACTION_OPEN;
                        } else {
                            // 如果是打开左侧边的情况下，则为关闭
                            preNextAction = SLIDE_PRE_ACTION_CLOSE;
                        }
                    }
                } else {
                    currVelocity = mVelocityTracker.getYVelocity();
                    directionMoveOffset = Math.abs(offsetY) - Math.abs(offsetX);
                    if (offsetY > 0) {
                        preNextAction = SLIDE_PRE_ACTION_OPEN;
                    } else if (offsetY < 0) {
                        preNextAction = SLIDE_PRE_ACTION_CLOSE;
                    }
                }
                if (!mIsScrolling && directionMoveOffset < ViewConfiguration.getTouchSlop()) {
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                mIsScrolling = true;
                switch (mSlideDirection) {
                    case SLIDE_HORIZONTAL:
                        doHorizontalMove(offsetX, scrollX);
                        break;
                    case SLIDE_VERTICAL:
                        doVerticalMove(offsetY, scrollY);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();
                switch (mSlideDirection) {
                    case SLIDE_HORIZONTAL:
                        doHorizontalUp(scrollX);
                        break;
                    case SLIDE_VERTICAL:
                        doVerticalUp(scrollY);
                        break;
                }
                preNextAction = SLIDE_PRE_ACTION_DEFAULT;
                //                mContentView.setScaleX(1f);
                //                mContentView.setScaleY(1f);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void doVerticalUp(float scrollY) {
        int finalScrollY = 0;
        if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
            if (Math.abs(scrollY) <= mSlideView.getMeasuredHeight() - getSlideCriticalValue()) {
                finalScrollY = 0;
            } else {
                finalScrollY = mSlideView.getMeasuredHeight();
            }
        } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
            if (Math.abs(scrollY) > getSlideCriticalValue()) {
                finalScrollY = mSlideView.getMeasuredHeight();
            } else {
                finalScrollY = 0;
            }
        }
        doTranYAnimator(mContentView, finalScrollY, true);
    }

    private void doHorizontalUp(float scrollX) {
        int finalScrollX = 0;
        if (scrollX < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 右侧边滑动---关闭
                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                if (Math.abs(
                        scrollX) <= mSlideView.getMeasuredWidth() - getSlideCriticalValue() || Math.abs(currVelocity) > VELOCITY_LIMIT_CLOSE_DEFAULT) {
                    finalScrollX = 0;
                } else {
                    finalScrollX = -mSlideView.getMeasuredWidth();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 右侧边滑动---开启
                // 向左滑
                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                if (Math.abs(scrollX) > getSlideCriticalValue() || Math.abs(currVelocity) > VELOCITY_LIMIT_OPEN_DEFAULT) {
                    finalScrollX = -mSlideView.getMeasuredWidth();
                } else {
                    finalScrollX = 0;
                }

            }
            doSlideViewAlphaAnimator(Math.abs(
                    finalScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())),
                                     true);
        } else if (scrollX > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 左边滑动删除---关闭
                finalScrollX = 0;
                doContentViewAlphaAnimator(1, true);
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 左边滑动删除---删除
                if (scrollX > getRemoveCriticalValue() || currVelocity > 2500) {
                    finalScrollX = 2 * getMeasuredWidth();
                    doContentViewAlphaAnimator(0, true);
                } else {
                    finalScrollX = 0;
                    doContentViewAlphaAnimator(1, true);
                }

            }
        }
        if(finalScrollX > 0 || (finalScrollX == 0 && preNextAction == SLIDE_PRE_ACTION_OPEN)) {
            return;
        }
        doTranXAnimator(mContentView, finalScrollX, true);
    }

    private void doVerticalMove(int offsetY, float scrollY) {
        float newScrollY = scrollY + offsetY;
        if (Math.abs(newScrollY) > mSlideView.getMeasuredHeight() - 1) {
            newScrollY = mSlideView.getMeasuredHeight();
        }
        doTranYAnimator(mContentView, newScrollY, false);
    }

    private void doHorizontalMove(int offsetX, float scrollX) {
        float newScrollX = scrollX + offsetX;
        if (newScrollX > 0) {
            return;
        }
        if (scrollX < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                if (Math.abs(newScrollX) > mSlideView.getMeasuredWidth() - 1) {
                    newScrollX = -mSlideView.getMeasuredWidth();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollX > 0) {
                    newScrollX = 0;
                }
            }
            doSlideViewAlphaAnimator(0.3f + 0.7f * Math.abs(
                    newScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())),
                                     false);
        } else if (scrollX > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollX < 0) {
                    newScrollX = 0;
                }
            }
            mSlideView.setAlpha(0f);
            doSlideViewAlphaAnimator(0, false);
            doContentViewAlphaAnimator(
                    1f - 1f * mContentView.getTranslationX() / mContentView.getMeasuredWidth(),
                    false);
        }
        doTranXAnimator(mContentView, newScrollX, false);
    }

    //TODO  when mSlideCriticalValue != 0, slide critical need fix.
    private int getSlideCriticalValue() {
        if (mSlideDirection == SLIDE_HORIZONTAL) {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenWidth / 10;
            }
        } else {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenHeight / 10;
            }
        }
        return mSlideCriticalValue;
    }

    private int getRemoveCriticalValue() {
        if (mSlideDirection == SLIDE_HORIZONTAL) {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenWidth / 2;
            }
        } else {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenHeight / 2;
            }
        }
        return mSlideCriticalValue;
    }

    private float finalSlideViewAlpha;
    private float finalContentViewAlpha;

    private void doContentViewAlphaAnimator(float alpha, boolean isNeedAnimator) {
        if (isNeedAnimator) {
            finalContentViewAlpha = alpha;
            if (null == alphaContentViewAnimator) {
                alphaContentViewAnimator = ObjectAnimator.ofFloat(mContentView, "alpha",
                                                                  mContentView.getAlpha(), alpha);
                alphaContentViewAnimator.setDuration(alphaAnimatorDuration);
                alphaContentViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mContentView.setAlpha(finalContentViewAlpha);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mContentView.setAlpha(finalContentViewAlpha);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                alphaContentViewAnimator.setFloatValues(mContentView.getAlpha(), alpha);
            }
            alphaContentViewAnimator.start();
        } else {
            mContentView.setAlpha(alpha);
        }
    }

    private void doSlideViewAlphaAnimator(float alpha, boolean isNeedAnimator) {
        if (isNeedAnimator) {
            finalSlideViewAlpha = alpha;
            if (null == alphaSlideViewAnimator) {
                alphaSlideViewAnimator = ObjectAnimator.ofFloat(mSlideView, "alpha",
                                                                mSlideView.getAlpha(), alpha);
                alphaSlideViewAnimator.setDuration(alphaAnimatorDuration);
                alphaSlideViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSlideView.setAlpha(finalSlideViewAlpha);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mSlideView.setAlpha(finalSlideViewAlpha);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                alphaSlideViewAnimator.setFloatValues(mSlideView.getAlpha(), alpha);
            }
            alphaSlideViewAnimator.start();
        } else {
            mSlideView.setAlpha(alpha);
        }
    }

    private void doTranXAnimator(final View view, float tranX, boolean isNeedAnimator) {
        Log.d("huruidong", "at ssui at com ---> doTranXAnimator() tranX: " + tranX + " " + isNeedAnimator);
        if (isNeedAnimator) {
            if (null == tranXSpringAnimation) {
                tranXSpringAnimation = new SpringAnimation(view, new FloatPropertyCompat<View>(
                        "translationX") {
                    @Override
                    public float getValue(View view) {
                        return view.getTranslationX();
                    }

                    @Override
                    public void setValue(View view, float value) {
                        Log.d("huruidong",
                              "at ssui at com ---> setValue() getMeasuredWidth(): " + getMeasuredWidth());
                        Log.d("huruidong", "at ssui at com ---> setValue() key: " + value);
                        view.setTranslationX(value);
                        if (value >= getMeasuredWidth()) {
//                            setVisibility(GONE);
                            mIsScrolling = false;
                            if (null != mListener) {
                                mListener.delete();
                            }
                        }
                    }
                });
                tranXSpringForce = new SpringForce(tranX);
                tranXSpringForce.setStiffness(SpringForce.STIFFNESS_MEDIUM);
                tranXSpringForce.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                tranXSpringAnimation.setSpring(tranXSpringForce);
                tranXSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
//                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                });
            } else {
                tranXSpringForce.setFinalPosition(tranX);
            }
            tranXSpringAnimation.start();
        } else {
            view.setTranslationX(tranX);
            if (tranX >= getMeasuredWidth()) {
//                setVisibility(GONE);
                mIsScrolling = false;
                if (null != mListener) {
                    mListener.delete();
                }
            }
        }
    }

    private void doTranYAnimator(final View view, float tranY, boolean isNeedAnimator) {
        if (isNeedAnimator) {
            if (null == tranYSpringAnimation) {
                tranYSpringAnimation = new SpringAnimation(view, new FloatPropertyCompat<View>(
                        "translationY") {

                    @Override
                    public float getValue(View view) {
                        return view.getTranslationY();
                    }

                    @Override
                    public void setValue(View view, float value) {
                        view.setTranslationY(value);
                    }
                });
                tranYSpringForce = new SpringForce(tranY);
                tranYSpringForce.setStiffness(SpringForce.STIFFNESS_MEDIUM);
                tranYSpringForce.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
                tranYSpringAnimation.setSpring(tranYSpringForce);
            } else {
                tranYSpringForce.setFinalPosition(tranY);
            }
            tranYSpringAnimation.start();
        } else {
            view.setTranslationX(tranY);
        }
    }

    public interface OnSlideActionListener {
        void delete();
    }
}
