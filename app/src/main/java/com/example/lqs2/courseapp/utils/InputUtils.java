package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘辅助类
 *
 * @author lqs2
 */
public class InputUtils {

    /**
     * 关闭edit的软键盘
     *
     * @param mEditText edit
     * @param mContext  上下文
     */
    public static void closeSoftKeyboard(EditText mEditText, Context mContext) {
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
