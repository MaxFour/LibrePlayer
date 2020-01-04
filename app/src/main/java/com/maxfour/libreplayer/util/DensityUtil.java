package com.maxfour.libreplayer.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

public class DensityUtil {

    public static int getScreenHeight(@NonNull Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int dip2px(@NonNull Context context, float dpVale) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVale * scale + 0.5f);
    }

}
