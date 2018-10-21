package com.shuishou.jslog.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.shuishou.jslog.ConstantValue;


/**
 * Created by Administrator on 2017/10/5.
 */

public class CommonTool {
    public static Message buildMessage(int what, Object obj, int arg1, int arg2){
        Message msg = new Message();
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public static Message buildMessage(int what, Object obj){
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    public static Message buildMessage(int what){
        Message msg = new Message();
        msg.what = what;
        return msg;
    }

    public static void popupToast(Context context, String msg, int shortlong){
        Toast.makeText(context, msg, shortlong).show();
    }

    public static void popupWarnDialog(Context context, int iconId, String title, String msg){
        new AlertDialog.Builder(context)
                .setIcon(iconId)
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("OK", null)
                .create().show();
    }

    public static String transferDouble2Scale(double d){
        return String.format(ConstantValue.FORMAT_DOUBLE, d);
    }

    /**
     * if d is positive, return d with 2 decimal
     * if d is negative, return -d with 2 decimal, surrounding the bracket()
     * @param d
     * @return
     */
    public static String transferNumberByPM(double d, String currencyIcon){
        if (d >= 0){
            if (currencyIcon != null)
                return transferDouble2Scale(d);
            else
                return currencyIcon + transferDouble2Scale(d);
        } else {
            if (currencyIcon != null)
                return "(" + currencyIcon + transferDouble2Scale(d * (-1)) + ")";
            else
                return "(" + transferDouble2Scale(d * (-1)) + ")";
        }
    }
    
    /**
     * 启动一个等待框,
     * @param context
     * @param title
     * @param message
     * @param closeTime 避免程序异常时, 等待框不会自己关闭, 通过这个时间, 设置其自动关闭
     */
    public static ProgressDialog startProgressDialog(Context context, String title, String message, int closeTime){
        final ProgressDialog progressDlg = ProgressDialog.show(context, title, message);
        //启动progress dialog后, 同时启动一个线程来关闭该process dialog, 以防系统未正常结束, 导致此progress dialog长时间卡主. 设定时间为15秒(超过request的timeout时间)
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (progressDlg != null)
                    progressDlg.dismiss();
            }
        };
        Handler progressDlgCanceller = new Handler();
        progressDlgCanceller.postDelayed(r, closeTime * 1000);
        return progressDlg;
    }
    
    public static void buildAlertDialog(Context context, String message, String title){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
            .setNegativeButton("OK", null)
            .setMessage(message)
            .setTitle(title);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
