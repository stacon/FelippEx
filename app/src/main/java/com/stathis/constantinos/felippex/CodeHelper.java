package com.stathis.constantinos.felippex;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class CodeHelper {
    protected static void showErrorDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Oops")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
