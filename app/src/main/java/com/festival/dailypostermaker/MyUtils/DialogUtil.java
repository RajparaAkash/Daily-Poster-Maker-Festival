package com.festival.dailypostermaker.MyUtils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.WindowManager;

public class DialogUtil {

    public static Dialog getDialogObject(Activity mContext, int layout) {
        try {
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(layout);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            return dialog;
        } catch (Exception e) {
            return null;
        }
    }


    public static Dialog getDialog(Activity mContext, int layout) {
        try {
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(layout);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            return dialog;
        } catch (Exception e) {
            return null;
        }
    }
}
