package com.janek.maowithfriends.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CardOverlapDecoration extends RecyclerView.ItemDecoration {

    private final static int overlap = -200;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int itemPosition = parent.getChildAdapterPosition(view);
//        outRect.set(overlap, 0, 0, 0);

        if (itemPosition == 0) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(overlap, 0, 0, 0);
        }
    }
}
