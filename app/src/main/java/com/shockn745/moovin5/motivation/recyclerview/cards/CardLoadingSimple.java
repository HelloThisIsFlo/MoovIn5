package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

/**
 * Card shown when the loading time is longer than loc_req_expiration / 2
 * Also the base class for
 * {@link com.shockn745.moovin5.motivation.recyclerview.cards.CardLoading}
 *
 * @author Florian Kempenich
 */
public class CardLoadingSimple extends AbstractCard {

    public static class LoadingSimpleVH extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public LoadingSimpleVH(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.loading_simple_text_view);
        }
    }

    private String mText;

    public CardLoadingSimple(Activity activity, String text) {
        super(activity);
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
