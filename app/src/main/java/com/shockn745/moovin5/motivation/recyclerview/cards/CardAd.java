package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

/**
 * Card that display ads
 *
 * @author Florian Kempenich
 */
public class CardAd implements CardInterface {

    public static class AdVH extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public AdVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.ad_text_view);
        }
    }

    private String mText;

    public CardAd(String text) {
        this.mText = "Ad : " + text;
    }

    @Override
    public int getViewType() {
        return AD_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return false;
    }

    public String getText() {
        return mText;
    }
}
