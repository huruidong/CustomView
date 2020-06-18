package com.example.customviewdemo.slideRecyclerview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * @author ljm
 * @date 2019/3/25
 * 侧滑菜单栏RecyclerView,交互流畅
 */
public class SlideRecyclerView extends RecyclerView {

    private boolean isNeedSlide = true;

    /**
     * 最小速度
     */
    private static final int MINIMUM_VELOCITY = 500;

    /**
     * 当前选中itemview
     */
    private ViewGroup mSelectedItemView;

    /**
     * 上次选中itemView
     */
    private ViewGroup mLastSelectedItemView;

    /**
     * 滑动的itemView
     */
    private View mMoveView;

    /**
     * 末次滑动的itemView
     */
    private View mLastMoveView;

    /**
     * itemView中菜单控件宽度
     */
    private int mMenuWidth;

    private VelocityTracker mVelocity;

    /**
     * 触碰时的首个横坐标
     */
    private int mFirstX;

    /**
     * 触碰时的首个纵坐标
     */
    private int mFirstY;

    /**
     * 触碰末次的横坐标
     */
    private int mLastX;

    /**
     * 最小滑动距离
     */
    private int mTouchSlop;

    /**
     * 是否正在水平滑动
     */
    private boolean mMoving;

    private SpringAnimation tranXSpringAnimation;
    private SpringForce     tranXSpringForce;

    public SlideRecyclerView(Context context) {
        super(context);
        init();
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isNeedSlide) {
            return super.onInterceptTouchEvent(e);
        }
        int x = (int) e.getX();
        int y = (int) e.getY();
        addVelocityEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != tranXSpringAnimation && tranXSpringAnimation.isRunning()) {
                    tranXSpringAnimation.skipToEnd();
                }
                mFirstX = x;
                mFirstY = y;
                mLastX = x;
                //获取点击区域所在的itemView
                mSelectedItemView = (ViewGroup) findChildViewUnder(x, y);
                mMoveView = mSelectedItemView.getChildAt(1);
                Log.d("huruidong",
                      "at ssui at com ---> onInterceptTouchEvent() \nmMoveView: " + mMoveView + "  \nmLastMoveView: " + mLastMoveView);
                //在点击区域以外的itemView开着菜单，则关闭菜单
                if (mLastMoveView != null && mLastMoveView != mMoveView && mLastMoveView.getTranslationX() != 0) {
                    closeMenuNow();
                }
                //获取itemView中菜单的宽度（规定itemView中为两个子View）
                if (mSelectedItemView != null && mSelectedItemView.getChildCount() == 2) {
                    mMenuWidth = mSelectedItemView.getChildAt(0).getWidth();
                } else {
                    mMenuWidth = -1;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocity.computeCurrentVelocity(1000);
                int velocityX = (int) Math.abs(mVelocity.getXVelocity());
                int velocityY = (int) Math.abs(mVelocity.getYVelocity());
                int moveX = Math.abs(x - mFirstX);
                int moveY = Math.abs(y - mFirstY);
                //满足如下条件其一则判定为水平滑动：
                //1、水平速度大于竖直速度,且水平速度大于最小速度
                //2、水平位移大于竖直位移,且大于最小移动距离
                //必需条件：itemView菜单栏宽度大于0，且recyclerView处于静止状态（即并不在竖直滑动和拖拽）
                boolean isHorizontalMove = (Math.abs(
                        velocityX) >= MINIMUM_VELOCITY && velocityX > velocityY || moveX > moveY && moveX > mTouchSlop) && mMenuWidth > 0 && getScrollState() == 0;
                if (isHorizontalMove) {
                    //设置其已处于水平滑动状态，并拦截事件
                    mMoving = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                releaseVelocity();
                //itemView以及其子view触发触碰事件(点击、长按等)，菜单未关闭则直接关闭
                closeMenuNow();
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isNeedSlide) {
            return super.onTouchEvent(e);
        }
        int x = (int) e.getX();
        int y = (int) e.getY();
        addVelocityEvent(e);
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //若已处于水平滑动状态，则随手指滑动，否则进行条件判断
                if (mMoving) {
                    int dx = x - mLastX;
                    //让itemView在规定区域随手指移动
                    if (mMoveView.getTranslationX() + dx <= 0 && mMoveView.getTranslationX() + dx >= -mMenuWidth) {
                        doTranXAnimator(mMoveView, mMoveView.getTranslationX() + dx, false);
                    }
                    mLastX = x;
                    return true;
                } else {
                    mVelocity.computeCurrentVelocity(1000);
                    int velocityX = (int) Math.abs(mVelocity.getXVelocity());
                    int velocityY = (int) Math.abs(mVelocity.getYVelocity());
                    int moveX     = Math.abs(x - mFirstX);
                    int moveY     = Math.abs(y - mFirstY);
                    //根据水平滑动条件判断，是否让itemView跟随手指滑动
                    //这里重新判断是避免itemView中不拦截ACTION_DOWN事件，则后续ACTION_MOVE并不会走onInterceptTouchEvent()方法
                    boolean isHorizontalMove = (Math.abs(
                            velocityX) >= MINIMUM_VELOCITY && velocityX > velocityY || moveX > moveY && moveX > mTouchSlop) && mMenuWidth > 0 && getScrollState() == 0;
                    if (isHorizontalMove) {
                        int dx = x - mLastX;
                        //让itemView在规定区域随手指移动
                        if (mMoveView.getTranslationX() + dx <= 0 && mMoveView.getTranslationX() + dx >= -mMenuWidth) {
                            doTranXAnimator(mMoveView, mMoveView.getTranslationX() + dx, false);
                        }
                        mLastX = x;
                        //设置正处于水平滑动状态
                        mMoving = true;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mMoving) {
                    //先前没结束的动画终止，并直接到终点
                    mMoving = false;
                    //已放手，即现滑动的itemView成了末次滑动的itemView
                    mLastMoveView = mMoveView;
                    mLastSelectedItemView = mSelectedItemView;
                    mVelocity.computeCurrentVelocity(1000);
                    float scrollX = mLastMoveView.getTranslationX();
                    //若速度大于正方向最小速度，则关闭菜单栏；若速度小于反方向最小速度，则打开菜单栏
                    //若速度没到判断条件，则对菜单显示的宽度进行判断打开/关闭菜单
                    if (mVelocity.getXVelocity() >= MINIMUM_VELOCITY) {
                        doTranXAnimator(mMoveView, 0, true);
                    } else if (mVelocity.getXVelocity() <= -MINIMUM_VELOCITY) {
                        doTranXAnimator(mMoveView, -mMenuWidth, true);
                    } else if (scrollX < -mMenuWidth / 2) {
                        doTranXAnimator(mMoveView, -mMenuWidth, true);
                    } else {
                        doTranXAnimator(mMoveView, 0, true);
                    }
                    invalidate();
                } else if (mLastMoveView != null && mLastMoveView.getTranslationX() != 0) {
                    //若不是水平滑动状态，菜单栏开着则关闭
                    closeMenu();
                }
                releaseVelocity();
                break;
            default:
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 使用Scroller关闭菜单栏
     */
    public void closeMenu() {
        if (null != mLastMoveView) {
            doTranXAnimator(mLastMoveView, 0, true);
        }
    }

    /**
     * 即刻关闭菜单栏
     */
    public void closeMenuNow() {
        if (null != mLastMoveView) {
            doTranXAnimator(mLastMoveView, 0, false);
        }
    }

    /**
     * 获取VelocityTracker实例，并为其添加事件
     *
     * @param e 触碰事件
     */
    private void addVelocityEvent(MotionEvent e) {
        if (mVelocity == null) {
            mVelocity = VelocityTracker.obtain();
        }
        mVelocity.addMovement(e);
    }

    /**
     * 释放VelocityTracker
     */
    private void releaseVelocity() {
        if (mVelocity != null) {
            mVelocity.clear();
            mVelocity.recycle();
            mVelocity = null;
        }
    }

    /**
     * 判断该itemView是否显示在屏幕内
     *
     * @param view itemView
     * @return isInWindow
     */
    private boolean isInWindow(View view) {
        if (getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager         = (LinearLayoutManager) getLayoutManager();
            int                 firstPosition   = manager.findFirstVisibleItemPosition();
            int                 lastPosition    = manager.findLastVisibleItemPosition();
            int                 currentPosition = manager.getPosition(view);
            return currentPosition >= firstPosition && currentPosition <= lastPosition;
        }
        return true;
    }

    private void doTranXAnimator(final View view, float tranX, boolean isNeedAnimator) {
        Log.d("huruidong",
              "at ssui at com ---> doTranXAnimator() " + "\nview: " + view + "\ntranX: " + tranX + "\nisNeedAnimator" + isNeedAnimator);
        if (isNeedAnimator) {
            //            if (null == tranXSpringAnimation) {
            tranXSpringAnimation = new SpringAnimation(view, new FloatPropertyCompat<View>(
                    "translationX") {
                @Override
                public float getValue(View view) {
                    return view.getTranslationX();
                }

                @Override
                public void setValue(View view, float value) {
                    view.setTranslationX(value);
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
            //            } else {
            //                tranXSpringForce.setFinalPosition(tranX);
            //            }
            tranXSpringAnimation.start();
        } else {
            view.setTranslationX(tranX);
        }
    }

}