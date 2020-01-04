package com.maxfour.libreplayer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.maxfour.libreplayer.R;

public class NetworkImageView extends CircularImageView {

    public NetworkImageView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public NetworkImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NetworkImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setImageUrl(@NonNull String imageUrl) {
        setImageUrl(getContext(), imageUrl);
    }

    public void setImageUrl(@NonNull Context context, @NonNull String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_account_white_24dp)
                .placeholder(R.drawable.ic_account_white_24dp)
                .into(this);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray attributes = context.obtainStyledAttributes(attributeSet, R.styleable.NetworkImageView, 0, 0);
        String url = attributes.getString(R.styleable.NetworkImageView_url_link);
        setImageUrl(context, url);
        attributes.recycle();
    }
}
