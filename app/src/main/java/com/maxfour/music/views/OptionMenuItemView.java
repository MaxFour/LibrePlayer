package com.maxfour.music.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.maxfour.appthemehelper.ThemeStore;
import com.maxfour.appthemehelper.util.ColorUtil;
import com.maxfour.music.R;

public class OptionMenuItemView extends FrameLayout {

    TextView textView;
    AppCompatImageView iconImageView;

    public OptionMenuItemView(@NonNull Context context) {
        this(context, null);
    }

    public OptionMenuItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public OptionMenuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public OptionMenuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        int accentColor = ThemeStore.Companion.accentColor(context);
        setBackground(ContextCompat.getDrawable(context, R.drawable.menu_item_background));

        setClickable(true);
        setFocusable(true);

        inflate(context, R.layout.item_option_menu, this);

        setBackgroundTintList(ColorStateList.valueOf(ColorUtil.INSTANCE.adjustAlpha(accentColor, 0.22f)));

        textView = findViewById(R.id.title);
        iconImageView = findViewById(R.id.icon);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionMenuItemView, 0, 0);

        String title = attributes.getString(R.styleable.OptionMenuItemView_optionTitle);
        textView.setText(title);

        Drawable icon = attributes.getDrawable(R.styleable.OptionMenuItemView_optionIcon);
        iconImageView.setImageDrawable(icon);

        attributes.recycle();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            int accentColor = ThemeStore.Companion.accentColor(getContext());
            textView.setTextColor(accentColor);
            iconImageView.setImageTintList(ColorStateList.valueOf(accentColor));
        }
    }
}