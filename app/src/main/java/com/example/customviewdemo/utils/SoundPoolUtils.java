package com.example.customviewdemo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.ArrayMap;

public class SoundPoolUtils {

    private static final int MAX_STREAMS = 2;
    private static final int DEFAULT_QUALITY = 0;
    private static final int DEFAULT_PRIORITY = 1;
    private static final int LEFT_VOLUME = 1;
    private static final int RIGHT_VOLUME = 1;
    private static final int LOOP = 0;
    private static final float RATE = 1.0f;

    private static SoundPoolUtils sSoundPoolUtils;

    private ArrayMap<Integer, Integer> mSoundCache = new ArrayMap<>();

    private SoundPool mSoundPool;
    private Context   mContext;
    private Vibrator  mVibrator;


    private SoundPoolUtils(Context context) {
        mContext = context;
        intSoundPool();
        initVibrator();
    }

    /**
     * 初始化短音频的内容
     */
    private void intSoundPool() {
        //根据不同的版本进行相应的创建
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, DEFAULT_QUALITY);
        }
    }

    /**
     * 初始化震动的对象
     */
    private void initVibrator() {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static SoundPoolUtils getInstance(Context context) {
        if (sSoundPoolUtils == null) {
            synchronized (SoundPoolUtils.class) {
                if (sSoundPoolUtils == null) {
                    sSoundPoolUtils = new SoundPoolUtils(context);
                }
            }
        }
        return sSoundPoolUtils;
    }

    /**
     * @param resId 音频的资源ID
     *              开始播放音频
     */
    public void playVideo(int resId) {
        if (mSoundPool == null) {
            intSoundPool();
        }
        int load = loadVideo(resId);
        mSoundPool.play(load, LEFT_VOLUME, RIGHT_VOLUME, DEFAULT_PRIORITY, LOOP, RATE);
    }

    public int loadVideo(int resId) {
        if (mSoundPool == null) {
            intSoundPool();
        }
        Object cacheObj = mSoundCache.get(resId);
        if (null != cacheObj) {
            return (int) cacheObj;
        } else {
            int loadId = mSoundPool.load(mContext, resId, DEFAULT_PRIORITY);
            mSoundCache.put(resId, loadId);
            return loadId;
        }
    }

    public void playVideoByLoad(int load) {
        if (mSoundPool == null) {
            intSoundPool();
        }
        mSoundPool.play(load, LEFT_VOLUME, RIGHT_VOLUME, DEFAULT_PRIORITY, LOOP, RATE);
    }

    /**
     * @param milliseconds 震动时间
     *                     开启相应的震动
     */
    public void startVibrator(long milliseconds) {
        startVibrator(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE);
    }

    /**
     * @param milliseconds 震动时间
     * @param amplitude    震动强度
     *                     开启相应的震动
     */
    public void startVibrator(long milliseconds, int amplitude) {
        if (mVibrator == null) {
            initVibrator();
        }
        if (mVibrator.hasVibrator()) {
            mVibrator.cancel();
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude);
            mVibrator.vibrate(vibrationEffect);
        }
    }

    /**
     * @param resId        资源id
     * @param milliseconds 震动时间
     *                     同时开始音乐和震动
     */
    public void startVideoAndVibrator(int resId, long milliseconds) {
        playVideo(resId);
        startVibrator(milliseconds);
    }

    public void startVideoAndVibrator(int resId, long milliseconds, int amplitude) {
        playVideo(resId);
        startVibrator(milliseconds, amplitude);
    }

    /**
     * 释放相应的资源
     */
    public void release() {
        //释放所有的资源
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }

        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}
