package com.janek.maowithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FirebaseCardViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.cardImageView) ImageView cardImageView;

    public View cardView;

    private Context context;

    public FirebaseCardViewHolder(View itemView) {
        super(itemView);
        cardView = itemView;
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();

        itemView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    Log.d("test", "started");
                    v.setVisibility(View.GONE);
                }
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    Log.d("test", "stopped");

                    v.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    public void bindCard(Card card) {
        int cardImgResource = context.getResources().getIdentifier(card.cardImage(), "drawable", context.getPackageName());
        Picasso.with(context).load(cardImgResource).fit().into(cardImageView);
    }
}