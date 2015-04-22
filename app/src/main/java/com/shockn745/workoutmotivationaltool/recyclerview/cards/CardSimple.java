package com.shockn745.workoutmotivationaltool.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Created by Shock on 21.04.15.
 */
public class CardSimple implements CardInterface{

    private String simpleText;

    public static class SimpleVH extends RecyclerView.ViewHolder {
        public TextView mSimpleTextView;

        public SimpleVH(View itemView) {
            super(itemView);
            this.mSimpleTextView = (TextView) itemView.findViewById(R.id.simple_text_view);
        }
    }

    public CardSimple(String mText) {
        this.simpleText = mText;
    }

    @Override
    public int getViewType() {
        return CardInterface.SIMPLE_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }
}
