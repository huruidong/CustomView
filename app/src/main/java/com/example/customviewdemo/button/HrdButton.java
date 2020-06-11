package com.example.customviewdemo.button;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class HrdButton extends Button {

    private ObjectAnimator zoomInXAnimator, zoomInYAnimator, zoomOutXAnimator, zoomOutYAnimator;

    private float mZoomScalePress = 0.9f;
    private int mZoomInDurationPress = 200;
    private int mZoomOutDurationPress = 35;

    public HrdButton(Context context) {
        this(context, null);
    }

    public HrdButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public HrdButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HrdButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setmZoomScalePress(float mZoomScalePress) {
        this.mZoomScalePress = mZoomScalePress;
    }

    public void setmZoomInDurationPress(int mZoomInDurationPress) {
        this.mZoomInDurationPress = mZoomInDurationPress;
    }

    public void setmZoomOutDurationPress(int mZoomOutDurationPress) {
        this.mZoomOutDurationPress = mZoomOutDurationPress;
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
}
