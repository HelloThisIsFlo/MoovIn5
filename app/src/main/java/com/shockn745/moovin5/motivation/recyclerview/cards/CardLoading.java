package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shockn745.moovin5.R;

/**
 * Card shown when the application is processing. Just after entering MotivationActivity.
 */
public class CardLoading extends CardLoadingSimple {

    public static class LoadingVH extends RecyclerView.ViewHolder {
        public final ProgressBar mProgressBar;
        public final TextView mTextView;

        public LoadingVH(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loading_progress_bar);
            mTextView = (TextView) itemView.findViewById(R.id.loading_text_view);
        }
    }

    public CardLoading(String text) {
        super(text);
    }

    @Override
    public int getViewType() {
        return LOADING_VIEW_TYPE;
    }

}
