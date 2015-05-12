package com.shockn745.workoutmotivationaltool.motivation.add_card_menu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

import java.util.ArrayList;

/**
 * Created by Shock on 13.05.15.
 */
public class AddCardMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class VH extends RecyclerView.ViewHolder {
        public TextView textView;

        public VH(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.add_card_menu_list_item_text_view);
        }
    }

    // Stores the VIEW_TYPES of cards that have been dismissed
    private final ArrayList<Integer> mListDismissedCards;

    public AddCardMenuAdapter(ArrayList<Integer> listDismissedCards) {
        this.mListDismissedCards = listDismissedCards;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.add_card_menu_list_item, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((VH) holder).textView.setText("TEMP");
        // TODO implement for real
    }

    @Override
    public int getItemCount() {
        return mListDismissedCards.size();
    }
}
