package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

/**
 * Card that display ads
 *
 * @author Florian Kempenich
 */
public class CardAd extends AbstractCard {

    public static class AdVH extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public AdVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.ad_text_view);
        }
    }

    private String mText;

    public CardAd(Activity activity, String text) {
        super(activity);
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
