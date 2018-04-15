package com.stathis.constantinos.felippex;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CodeHelper {
    protected static void showErrorDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Oops")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

        public static String getDateNowToString(){
            DateFormat dateFormatDate = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            return dateFormatDate.format(date);
        }

        public static String getTimeNowToString(){
            DateFormat dateFormatDate = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            return dateFormatDate.format(date);
        }

        public static String getDateTimeNowToString(){
            DateFormat dateFormatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            return dateFormatDate.format(date);
        }

}
