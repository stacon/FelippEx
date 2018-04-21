package com.stathis.constantinos.felippex;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CodeHelper {
    // Shows Error dialog on current activity
    protected static void showErrorDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Oops")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Returns date as String yyyy/MM/dd
    public static String getDateNowToString() {
        DateFormat dateFormatDate = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormatDate.format(date);
    }

    // Returns time as String HH:mm:ss
    public static String getTimeNowToString() {
        DateFormat dateFormatDate = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormatDate.format(date);
    }

    // Returns dateTime as String yyyy/MM/dd HH:mm:ss
    public static String getDateTimeNowToString() {
        DateFormat dateFormatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormatDate.format(date);
    }

    // Return boolean after checking the validity of an e-mail
    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Checks if values are empty and produces errors for LoginActivity
    public final static boolean emailAndPasswordValid(String email, String password, Context context) {
        Boolean valid = true;

        if (email.isEmpty()) {
            Toast.makeText(context, R.string.request_email_fill_toast, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!CodeHelper.isValidEmail(email)) {
            Toast.makeText(context, R.string.email_appears_invalid_toast, Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (password.equals("")) {
            Toast.makeText(context, R.string.request_password_fill_toast + email, Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

}
