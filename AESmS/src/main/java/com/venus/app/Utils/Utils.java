package com.venus.app.Utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public abstract class Utils {


    public static boolean isGoodStringValue(String s) {
        if (s == null) return false;
        while (s.startsWith(" ") || s.startsWith("\n"))
            s = s.substring(1);
        return !s.isEmpty();
    }

    public static ProgressDialog newLoadingDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);
        dialog.setMessage("Chargement...");
        return dialog;
    }




}
