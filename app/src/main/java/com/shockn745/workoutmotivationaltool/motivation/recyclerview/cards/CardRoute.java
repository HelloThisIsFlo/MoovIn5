package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Card that display route
 */
public class CardRoute implements CardInterface {

    public static class RouteVH extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public RouteVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.route_text_view);
        }
    }

    private String mText;

    public CardRoute(String text) {
        this.mText = "Route : " + text;
    }

    @Override
    public int getViewType() {
        return ROUTE_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getText() {
        return mText;
    }
}
