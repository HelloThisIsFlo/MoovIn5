package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Card displaying the time back at home
 *
 * @author Florian Kempenich
 */
public class CardBackAtHome extends AbstractCard {

    public static class BackAtHomeVH extends RecyclerView.ViewHolder {
        public final TextView mBackAtHomeTimeTextView;
        public final TextView mMotivationalTextView;

        public BackAtHomeVH(View itemView) {
            super(itemView);
            mBackAtHomeTimeTextView = (TextView)
                    itemView.findViewById(R.id.back_at_home_time_text_view);
            mMotivationalTextView = (TextView)
                    itemView.findViewById(R.id.back_at_home_motivational_text_view);
        }
    }

    private final String mBackAtHomeTimeString;
    private final String mMotivationalString;

    public CardBackAtHome(Activity activity, Date backAtHomeTime) {
        super(activity);
        // Format backAtHome time
        this.mBackAtHomeTimeString = DateFormat
                .getTimeFormat(activity)
                .format(backAtHomeTime);

        // Pick 3 random motivational string from string array
        String[] motivationalArray = activity
                .getResources()
                .getStringArray(R.array.back_at_home_card_motivational_array);

        // Generate 3 random positions
        ArrayList<Integer> randomInt = new ArrayList<>(motivationalArray.length);
        for (int i = 0; i < motivationalArray.length; i++) {
            randomInt.add(i);
        }
        Collections.shuffle(randomInt);

        String htmlBulleted =
                "&nbsp;&nbsp;&nbsp;&nbsp; &#8226; " + motivationalArray[randomInt.get(0)]
                        + "<br/>"
                + "&nbsp;&nbsp;&nbsp;&nbsp; &#8226; " + motivationalArray[randomInt.get(1)]
                        + "<br/>"
                + "&nbsp;&nbsp;&nbsp;&nbsp; &#8226; " + motivationalArray[randomInt.get(2)];

        mMotivationalString = activity.getString(R.string.back_at_home_card_motivational_intro)
                + Html.fromHtml(htmlBulleted);

    }

    @Override
    public int getViewType() {
        return BACK_AT_HOME_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return false;
    }

    public String getBackAtHomeTimeString() {
        return mBackAtHomeTimeString;
    }

    public String getMotivationalString() {
        return mMotivationalString;
    }
}
