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
import android.os.Build;
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

    private static final int SLIDE_START  = 1;
    private static final int SLIDE_END    = 2;
    private static final int SLIDE_TOP    = 3;
    private static final int SLIDE_BOTTOM = 4;

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
        mSlideDirection = typedArray.getInt(R.styleable.SlideLayout_slideDirection, SLIDE_END);
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
            float scrollOffset = (mSlideDirection == SLIDE_START || mSlideDirection == SLIDE_END) ? mContentView.getTranslationX() : mContentView.getTranslationY();
            retValue = (scrollOffset == 0) ? STATE_CLOSE : STATE_OPEN;
        }
        return retValue;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSlideView = findViewById(R.id.hrd_slidelayout_slide);
        mContentView = findViewById(R.id.hrd_slidelayout_content);
        if ((null == mSlideView && null != mContentView) || (null == mContentView && null != mSlideView)) {
            if (getChildCount() != 2) {
                throw new IllegalArgumentException(
                        "SlideLayout only need contains two child (content(R.id.hrd_slidelayout_content) and slide(R.id.hrd_slidelayout_slide)).");
            } else {
                if (null == mSlideView) {
                    int mContentViewIndex = indexOfChild(mContentView);
                    int mSlideViewIndex   = 1 - mContentViewIndex;
                    mSlideView = getChildAt(mSlideViewIndex);
                } else if (null == mContentView) {
                    int mSlideViewIndex   = indexOfChild(mSlideView);
                    int mContentViewIndex = 1 - mSlideViewIndex;
                    mContentView = getChildAt(mContentViewIndex);
                }
            }
        } else if (null == mSlideView && null == mContentView) {
            if (getChildCount() != 2) {
                throw new IllegalArgumentException(
                        "SlideLayout only need contains two child ," + "\nrule 1:the first is slideView and the second is contentView" + "\nrule2:(content(R.id.hrd_slidelayout_content) and slide(R.id.hrd_slidelayout_slide)).");
            } else {
                mSlideView = getChildAt(0);
                mContentView = getChildAt(1);
            }
        }
        mSlideView.setAlpha(0);
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
            case SLIDE_START:
                if (isLayoutRtl()) {
                    mSlideView.layout(getMeasuredWidth() - mSlideView.getMeasuredWidth(), 0,
                                      getMeasuredWidth(), getMeasuredHeight());
                } else {
                    mSlideView.layout(0, 0, mSlideView.getMeasuredWidth(), getMeasuredHeight());
                }
                break;
            case SLIDE_END:
                if (isLayoutRtl()) {
                    mSlideView.layout(0, 0, mSlideView.getMeasuredWidth(), getMeasuredHeight());
                } else {

                    mSlideView.layout(getMeasuredWidth() - mSlideView.getMeasuredWidth(), 0,
                                      getMeasuredWidth(), getMeasuredHeight());
                }
                break;
            case SLIDE_TOP:
                mSlideView.layout(0, 0, mSlideView.getMeasuredWidth(), mSlideView.getMeasuredHeight());
                break;
            case SLIDE_BOTTOM:
                mSlideView.layout(0, getMeasuredHeight() - mSlideView.getMeasuredHeight(),
                                  getMeasuredWidth(), getMeasuredHeight());
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
        Log.d("huruidong", "at ssui at com ---> dispatchTouchEvent() key: " + event.getAction());
        mVelocityTracker.addMovement(event);
        int   eventX   = (int) event.getX();
        int   eventY   = (int) event.getY();
        int   offsetX  = eventX - mLastX;
        int   offsetY  = eventY - mLastY;
        float curTranX = mContentView.getTranslationX();
        float curTranY = mContentView.getTranslationY();
        mLastX = eventX;
        mLastY = eventY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsScrolling = false;
                super.dispatchTouchEvent(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                int[] states = {android.R.attr.enabled};
                if (null != getBackground()) {
                    getBackground().setState(states);
                }
                // 检查在当前设置的侧滑方向上，该次滑动是否有效
                Log.d("huruidong",
                      "at ssui at com ---> dispatchTouchEvent() key: " + checkValidMoveByDirection(
                              offsetX, offsetY));
                if (checkValidMoveByDirection(offsetX, offsetY)) {
                    return super.dispatchTouchEvent(event);
                }
                mVelocityTracker.computeCurrentVelocity(1000); //设置units的值为1000，意思为一秒时间内运动了多少个像素
                int directionMoveOffset = 0;
                // 根据侧滑方向计算滑动速度、滑动距离以及预测该次滑动是开启还是关闭菜单
                if (mSlideDirection == SLIDE_START || mSlideDirection == SLIDE_END) {
                    currVelocity = mVelocityTracker.getXVelocity();
                    directionMoveOffset = Math.abs(offsetX) - Math.abs(offsetY);
                    setPreNextAction(offsetX, curTranX);
                } else if (mSlideDirection == SLIDE_TOP || mSlideDirection == SLIDE_BOTTOM) {
                    currVelocity = mVelocityTracker.getYVelocity();
                    directionMoveOffset = Math.abs(offsetY) - Math.abs(offsetX);
                    setPreNextAction(offsetY, curTranY);
                }
                if (!mIsScrolling && directionMoveOffset < ViewConfiguration.getTouchSlop()) {
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                mIsScrolling = true;
                switch (mSlideDirection) {
                    case SLIDE_START:
                    case SLIDE_END:
                        Log.d("huruidong",
                              "at ssui at com ---> dispatchTouchEvent() offsetX: " + offsetX + "  curTranX: " + curTranX);
                        doHorizontalMove(offsetX, curTranX);
                        break;
                    case SLIDE_TOP:
                    case SLIDE_BOTTOM:
                        doVerticalMove(offsetY, curTranY);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();
                switch (mSlideDirection) {
                    case SLIDE_START:
                    case SLIDE_END:
                        doHorizontalUp(curTranX);
                        break;
                    case SLIDE_TOP:
                    case SLIDE_BOTTOM:
                        doVerticalUp(curTranY);
                        break;
                }
                preNextAction = SLIDE_PRE_ACTION_DEFAULT;
                //                mContentView.setScaleX(1f);
                //                mContentView.setScaleY(1f);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean checkValidMoveByDirection(int offsetX, int offsetY) {
        if (mSlideDirection == SLIDE_END) {
            // 如果是从END滑出
            if (isLayoutRtl()) {
                // rtl布局，end为从左边滑出菜单，手势为从左向右滑动
                if (mContentView.getTranslationX() <= 0 && offsetX < 0) {
                    // 如果mContentView（静止或已经向左边滑动）且手指在向左边滑动，则不作处理
                    return true;
                }
            } else {
                // 正常布局，end为从右侧滑出菜单，手势为从右向左滑动
                if (mContentView.getTranslationX() >= 0 && offsetX > 0) {
                    // 如果mContentView（静止或已经向右边滑动）且手指在向右边滑动，则不作处理
                    return true;
                }
            }
        } else if (mSlideDirection == SLIDE_START) {
            // 如果是从START滑出
            if (isLayoutRtl()) {
                // rtl布局，START为从右侧滑出菜单，手势为从右向左滑动
                if (mContentView.getTranslationX() >= 0 && offsetX > 0) {
                    // 如果mContentView（静止或已经向右边滑动）且手指在向右边滑动，则不作处理
                    return true;
                }
            } else {
                // 正常布局，start为左侧滑出菜单，手势为从左向右滑动
                if (mContentView.getTranslationX() <= 0 && offsetX < 0) {
                    // 如果mContentView（静止或已经向左边滑动）且手指在向左边滑动，则不作处理
                    return true;
                }
            }
        } else if (mSlideDirection == SLIDE_TOP) {
            // 如果为TOP，则为上方滑出菜单，手势为从上向下滑动
            if (mContentView.getTranslationY() <= 0 && offsetY < 0) {
                // 如果mContentView（静止或已经向上滑动）且手指正在向上滑动，则不作处理
                return true;
            }
        } else if (mSlideDirection == SLIDE_BOTTOM) {
            // 如果为BOTTOM，则为下方滑出菜单，手势为从下向上滑动
            if (mContentView.getTranslationY() >= 0 && offsetY > 0) {
                // 如果mContentView（静止或已经向下滑动）且手指正在向下滑动，则不作处理
                return true;
            }
        }
        return false;
    }

    private void setPreNextAction(int offset, float curTran) {
        if (offset > 0) {
            // 从左向右滑动时→→→ || 从上往下滑动时↓↓↓
            if (curTran >= 0) {
                // 如果是默认状态或者打开左侧边的情况下 || 如果是默认状态或者打开上侧菜单的情况下，则为开启
                preNextAction = SLIDE_PRE_ACTION_OPEN;
            } else {
                // 如果是已经打开右侧边的情况下 || 如果是打开下侧菜单的情况下, 则为关闭
                preNextAction = SLIDE_PRE_ACTION_CLOSE;
            }
        } else if (offset < 0) {
            // 从右向左滑动时←←← || 从下往上滑动时↑↑↑
            if (curTran <= 0) {
                // 如果是默认状态或者打开右侧边的情况下 || 如果是默认状态或者打开下侧菜单的情况下，则为打开
                preNextAction = SLIDE_PRE_ACTION_OPEN;
            } else {
                // 如果是打开左侧边的情况下 || 如果是打开上侧菜单的情况下，则为关闭
                preNextAction = SLIDE_PRE_ACTION_CLOSE;
            }
        }
    }

    private void doVerticalUp(float curTranY) {
        int finalScrollY = 0;
        if (curTranY > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 预测当前操作为纵向关闭操作时
                if (mSlideView.getMeasuredHeight() - Math.abs(
                        curTranY) >= getSlideCriticalValue()) {
                    // 如果当前滑动距离大于等于临界值，则关闭
                    finalScrollY = 0;
                } else {
                    // 如果没有，则保持开启
                    finalScrollY = mSlideView.getMeasuredHeight();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 预测当前操作为纵向开启操作时
                if (Math.abs(curTranY) > getSlideCriticalValue()) {
                    // 如果当前滑动距离大于等于临界值，则开启
                    finalScrollY = mSlideView.getMeasuredHeight();
                } else {
                    // 如果没有，则保持关闭
                    finalScrollY = 0;
                }
            }
        } else if (curTranY < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 预测当前操作为纵向关闭操作时
                if (mSlideView.getMeasuredHeight() - Math.abs(
                        curTranY) >= getSlideCriticalValue()) {
                    // 如果当前滑动距离大于等于临界值，则关闭
                    finalScrollY = 0;
                } else {
                    // 如果没有，则保持开启
                    finalScrollY = -mSlideView.getMeasuredHeight();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 预测当前操作为纵向开启操作时
                if (Math.abs(curTranY) > getSlideCriticalValue()) {
                    // 如果当前滑动距离大于等于临界值，则开启
                    finalScrollY = -mSlideView.getMeasuredHeight();
                } else {
                    // 如果没有，则保持关闭
                    finalScrollY = 0;
                }
            }
        }
        doSlideViewAlphaAnimator(Math.abs(
                finalScrollY * 1f / (mSlideView.getMeasuredHeight() - getSlideCriticalValue())),
                                 true);
        doTranYAnimator(mContentView, finalScrollY, true);
        invalidate();
    }

    private void doHorizontalUp(float curTranX) {
        int finalScrollX = 0;
        if (curTranX < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 右侧边滑动---关闭
                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                if (Math.abs(
                        curTranX) <= mSlideView.getMeasuredWidth() - getSlideCriticalValue() || Math.abs(
                        currVelocity) > VELOCITY_LIMIT_CLOSE_DEFAULT) {
                    finalScrollX = 0;
                } else {
                    finalScrollX = -mSlideView.getMeasuredWidth();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 右侧边滑动---开启
                // 向左滑
                // 滑动距离小于临界值时收起不显示，大于或等于时展开显示
                Log.d("huruidong",
                      "at ssui at com ---> doHorizontalUp() " + "\ncurTranX: " + curTranX + "\ngetSlideCriticalValue: " + getSlideCriticalValue() + "\ncurrVelocity: " + currVelocity);
                if (Math.abs(curTranX) > getSlideCriticalValue() || Math.abs(
                        currVelocity) > VELOCITY_LIMIT_OPEN_DEFAULT) {
                    finalScrollX = -mSlideView.getMeasuredWidth();
                } else {
                    finalScrollX = 0;
                }

            }
            Log.d("huruidong", "at ssui at com ---> doHorizontalUp() key: " + Math.abs(
                    finalScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())));
            doSlideViewAlphaAnimator(Math.abs(
                    finalScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())),
                                     true);
        } else if (curTranX > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                // 左边滑动---关闭
                if (Math.abs(
                        curTranX) <= mSlideView.getMeasuredWidth() - getSlideCriticalValue() || Math.abs(
                        currVelocity) > VELOCITY_LIMIT_CLOSE_DEFAULT) {
                    finalScrollX = 0;
                } else {
                    finalScrollX = mSlideView.getMeasuredWidth();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                // 左边滑动---开启
                if (Math.abs(curTranX) > getSlideCriticalValue() || Math.abs(
                        currVelocity) > VELOCITY_LIMIT_OPEN_DEFAULT) {
                    finalScrollX = mSlideView.getMeasuredWidth();
                } else {
                    finalScrollX = 0;
                }
            }
        }
        doSlideViewAlphaAnimator(Math.abs(
                finalScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())),
                                 true);
        doTranXAnimator(mContentView, finalScrollX, true);
        invalidate();
    }

    private void doVerticalMove(int offsetY, float curTranY) {
        float newScrollY = curTranY + offsetY;
        if (curTranY < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                if (Math.abs(newScrollY) > mSlideView.getMeasuredHeight()) {
                    newScrollY = -mSlideView.getMeasuredHeight();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollY > 0) {
                    newScrollY = 0;
                }
            }
        } else if (curTranY > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                if (newScrollY > mSlideView.getMeasuredHeight()) {
                    newScrollY = mSlideView.getMeasuredHeight();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollY < 0) {
                    newScrollY = 0;
                }
            }
        }
        doSlideViewAlphaAnimator(Math.abs(
                newScrollY * 1f / (mSlideView.getMeasuredHeight() - getSlideCriticalValue())),
                                 false);
        doTranYAnimator(mContentView, newScrollY, false);
    }

    private void doHorizontalMove(int offsetX, float curTranX) {
        float newScrollX = curTranX + offsetX;
        if (curTranX < 0) {
            if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                if (Math.abs(newScrollX) > mSlideView.getMeasuredWidth() - 1) {

                    newScrollX = -mSlideView.getMeasuredWidth();
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollX > 0) {
                    newScrollX = 0;
                }
            }
        } else if (curTranX > 0) {
            if (preNextAction == SLIDE_PRE_ACTION_CLOSE) {
                if (newScrollX < 0) {
                    newScrollX = 0;
                }
            } else if (preNextAction == SLIDE_PRE_ACTION_OPEN) {
                if (newScrollX > mSlideView.getMeasuredWidth()) {
                    newScrollX = mSlideView.getMeasuredWidth();
                }
            }
        }
        doSlideViewAlphaAnimator(Math.abs(
                newScrollX * 1f / (mSlideView.getMeasuredWidth() - getSlideCriticalValue())),
                                 false);
        doTranXAnimator(mContentView, newScrollX, false);
    }

    //TODO  when mSlideCriticalValue != 0, slide critical need fix.
    private int getSlideCriticalValue() {
        if (mSlideDirection == SLIDE_START || mSlideDirection == SLIDE_END) {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenWidth / 10;
            }
        } else {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = 50;
            }
        }
        return mSlideCriticalValue;
    }

    private int getRemoveCriticalValue() {
        if (mSlideDirection == SLIDE_START || mSlideDirection == SLIDE_END) {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = screenWidth / 2;
            }
        } else {
            if (mSlideCriticalValue == 0) {
                mSlideCriticalValue = 50;
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
        Log.d("huruidong",
              "at ssui at com ---> doTranXAnimator() tranX: " + tranX + " " + isNeedAnimator);
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
            view.setTranslationY(tranY);
        }
    }

    public interface OnSlideActionListener {
        void delete();
    }

    public boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= 17) {
            return View.LAYOUT_DIRECTION_RTL == getResources().getConfiguration().getLayoutDirection();
        } else {
            return false;
        }
    }
}
