package com.maxfour.music.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class StatusBarView extends View {


    public StatusBarView(@NonNull Context context) {
        super(context);
    }

    public StatusBarView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static int getStatusBarHeight(@NonNull Resources r) {
        int result = 0;
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = r.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getStatusBarHeight(getResources()));
    }
}