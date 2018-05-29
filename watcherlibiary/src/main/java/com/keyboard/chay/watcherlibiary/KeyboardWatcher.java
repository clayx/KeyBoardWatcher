package com.keyboard.chay.watcherlibiary;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Chay on 2018/5/28
 * <p>
 * 键盘弹出监听，适配沉浸式状态栏匹配，
 * </p>
 */

public class KeyboardWatcher {
    private GlobalLayoutListener globalLayoutListener;
    private Context context;
    private View decorView;

    private final int HIDE_WHAT = 0;//隐藏时的延迟操作，防止用户可看到操作的现象

    public KeyboardWatcher(Context context) {
        this.context = context;
    }

    /**
     * 监听键盘的状态变化
     *
     * @param decorView
     * @param listener
     * @return
     */
    public KeyboardWatcher init(View decorView, OnKeyboardToggleListener listener) {
        this.decorView = decorView;
        this.globalLayoutListener = new GlobalLayoutListener(listener);
        addSoftKeyboardChangedListener();
        return this;
    }

    /**
     * 释放资源
     * 跟随Fragment和Activity生命周期
     */
    public void destroy() {
        removeSoftKeyboardChangedListener();
        globalLayoutListener = null;
    }

    /**
     * 取消软键盘状态变化监听
     */
    private void removeSoftKeyboardChangedListener() {
        if (globalLayoutListener != null && null != decorView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            } else {
                decorView.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
            }
        }
    }

    /**
     * 注册软键盘状态变化监听
     */
    private void addSoftKeyboardChangedListener() {
        if (globalLayoutListener != null && null != decorView) {
            removeSoftKeyboardChangedListener();
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    /**
     * 判断键盘是否显示
     *
     * @param context
     * @param decorView
     * @return
     */

    public Pair<Boolean, Integer> isKeyboardShowing(Context context, View decorView) {
        Rect outRect = new Rect();
        //指当前Window可看的实际区域，根据用户设置模式的不同，可以包含状态栏
        decorView.getWindowVisibleDisplayFrame(outRect);
        int displayScreenHeight = ScreenUtils.getDisplayScreenHeight(context);

        //如果屏幕高度和Window可见区域高度差值大于0，则表示软键盘显示中，否则软键盘为隐藏状态。
        int heightDifference = displayScreenHeight - outRect.bottom;
        return new Pair(heightDifference > 0, heightDifference);
    }


    public class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private boolean isKeyboardShow = false;

        private OnKeyboardToggleListener onKeyboardStateChangeListener;

        private Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case HIDE_WHAT:
                        if (!Preconditions.isNullOrEmpty(onKeyboardStateChangeListener)) {
                            onKeyboardStateChangeListener.onKeyboardClosed();
                        }
                        break;
                }
            }
        };

        public GlobalLayoutListener(OnKeyboardToggleListener onKeyboardStateChangeListener) {
            this.isKeyboardShow = false;
            this.onKeyboardStateChangeListener = onKeyboardStateChangeListener;
        }

        @Override
        public void onGlobalLayout() {
            if (null != onKeyboardStateChangeListener && null != decorView) {
                Pair<Boolean, Integer> pair = isKeyboardShowing(context, decorView);
                if (pair.first) {
                    isKeyboardShow = true;
                    onKeyboardStateChangeListener.onKeyboardShown(pair.second);
                } else if (isKeyboardShow) {
                    isKeyboardShow = false;
                    if (handler != null) {
                        //延迟处理，防止在关闭监听时出现闪动效果
                        handler.sendEmptyMessageDelayed(HIDE_WHAT, 50);
                    }
                }
            }
        }
    }

}
