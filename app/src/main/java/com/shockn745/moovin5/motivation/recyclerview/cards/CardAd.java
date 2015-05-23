package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shockn745.moovin5.R;

/**
 * Card that display ads
 *
 * @author Florian Kempenich
 */
public class CardAd extends AbstractCard {

    public static class AdVH extends RecyclerView.ViewHolder {
        public final FrameLayout mFrameLayout;

        public AdVH(View itemView) {
            super(itemView);
            this.mFrameLayout = (FrameLayout) itemView.findViewById(R.id.ad_card_container);
        }
    }


    public CardAd(Activity activity) {
        super(activity);
    }

    @Override
    public int getViewType() {
        return AD_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return false;
    }

}
