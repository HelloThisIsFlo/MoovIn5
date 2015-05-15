package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

/**
 * Card shown when the loading time is longer than loc_req_expiration / 2
 * Also the base class for
 * {@link com.shockn745.moovin5.motivation.recyclerview.cards.CardLoading}
 */
public class CardLoadingSimple implements CardInterface {

    public static class LoadingSimpleVH extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public LoadingSimpleVH(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.loading_simple_text_view);
        }
    }

    private String mText;

    public CardLoadingSimple(String text) {
        this.mText = text;
    }

    @Override
    public int getViewType() {
        return LOADING_SIMPLE_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return false;
    }

    public String getText() {
        return mText;
    }
}
