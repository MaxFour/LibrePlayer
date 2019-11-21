package com.maxfour.music.adapter.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.maxfour.appthemehelper.util.ATHUtil;
import com.maxfour.music.R;

public class MediaEntryViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    @Nullable
    public TextView title;

    @Nullable
    public TextView text;

    @Nullable
    public TextView time;

    @Nullable
    public TextView imageText;

    @Nullable
    public ViewGroup imageContainer;

    @Nullable
    public MaterialCardView imageContainerCard;

    @Nullable
    public View menu;

    @Nullable
    public View dragView;

    @Nullable
    public View paletteColorContainer;

    @Nullable
    public RecyclerView recyclerView;

    @Nullable
    public ImageButton playSongs;

    @Nullable
    public View mask;

    @Nullable
    public MaterialCardView imageTextContainer;

    @Nullable
    public ImageView image;

    public MediaEntryViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        text = itemView.findViewById(R.id.text);

        image = itemView.findViewById(R.id.image);
        time = itemView.findViewById(R.id.time);

        imageText = itemView.findViewById(R.id.imageText);
        imageContainer = itemView.findViewById(R.id.imageContainer);
        imageTextContainer = itemView.findViewById(R.id.imageTextContainer);
        imageContainerCard = itemView.findViewById(R.id.imageContainerCard);

        menu = itemView.findViewById(R.id.menu);
        dragView = itemView.findViewById(R.id.drag_view);
        paletteColorContainer = itemView.findViewById(R.id.paletteColorContainer);
        recyclerView = itemView.findViewById(R.id.recycler_view);
        mask = itemView.findViewById(R.id.mask);
        playSongs = itemView.findViewById(R.id.playSongs);

        if (imageContainerCard != null) {
            imageContainerCard.setCardBackgroundColor(ATHUtil.INSTANCE.resolveColor(itemView.getContext(), R.attr.colorPrimary));
        }
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    public void setImageTransitionName(@NonNull String transitionName) {
        if (image != null) {
            image.setTransitionName(transitionName);
        }
    }
}
