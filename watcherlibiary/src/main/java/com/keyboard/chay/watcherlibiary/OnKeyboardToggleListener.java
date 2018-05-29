package com.keyboard.chay.watcherlibiary;

/**
 * Created by Chay on 2018/5/28
 * <p>
 * 键盘show/hide监听
 * </p>
 */
public interface OnKeyboardToggleListener {

    void onKeyboardShown(int keyboardSize);

    void onKeyboardClosed();
}
