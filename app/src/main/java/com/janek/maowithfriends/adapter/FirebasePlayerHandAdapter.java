package com.janek.maowithfriends.adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.janek.maowithfriends.R;
import com.janek.maowithfriends.model.Card;
import com.janek.maowithfriends.ui.GameActivity;

public class FirebasePlayerHandAdapter extends FirebaseRecyclerAdapter<Card, FirebaseCardViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private int previousCont;

    public FirebasePlayerHandAdapter(Query ref, Context context) {
        super(Card.class, R.layout.player_hand_card_item, FirebaseCardViewHolder.class, ref);
        this.context = context;
        this.previousCont = getItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() > previousCont) {
            recyclerView.smoothScrollToPosition(getItemCount());
        }
        previousCont = getItemCount();
    }


//    @Override
//    public FirebaseCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        FirebaseCardViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
//        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
//                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//                ClipData dragData = new ClipData(v.getTag().toString(),
//                        mimeTypes, item);
//                v.setVisibility(View.GONE);
//                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
////                HeptagonDragShadowBuilder myShadow = new HeptagonDragShadowBuilder(Heptagon.this, 1.1f);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    v.startDragAndDrop(dragData, myShadow, null, 0);
//                } else {
//                    v.startDrag(dragData, myShadow, null, 0);
//                }
//                return true;
//            }
//        });
////        return super.onCreateViewHolder(parent, viewType);
//        return viewHolder;
//    }

    @Override
    protected void populateViewHolder(FirebaseCardViewHolder viewHolder, Card model, int position) {
        viewHolder.bindCard(model);
        viewHolder.cardView.setOnClickListener(view -> {
            ((GameActivity)context).playCard(position);
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
//                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//                ClipData dragData = new ClipData(v.getTag().toString(),
//                        mimeTypes, item);
                v.setVisibility(View.GONE);
                ClipData dragData = ClipData.newPlainText("", "");
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(dragData, myShadow, v, 0);
                } else {
                    v.startDrag(dragData, myShadow, v, 0);
                }
                return true;
            }
        });

//        viewHolder.itemView.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
//                    Log.d("test", "started");
//                    v.setVisibility(View.GONE);
//                }
//                if (event.getAction() == DragEvent.ACTION_DROP) {
//                    Log.d("test", "stopped");
//
//                    v.setVisibility(View.VISIBLE);
//                }
//                return false;
//            }
//        });
    }

}
