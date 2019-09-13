package com.example.im.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * 可以在子线程中显示toast
     * @param context
     * @param text
     */
    public static void showToastSafe(Context context,String text){
        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
