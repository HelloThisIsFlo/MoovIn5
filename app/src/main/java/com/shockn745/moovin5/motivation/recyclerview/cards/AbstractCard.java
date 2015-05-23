package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;

import com.shockn745.moovin5.R;

/**
 * Interface for the cards used in MotivationActivity
 *
 * @author Florian Kempenich
 */
public abstract class AbstractCard {

    public final static int LOADING_VIEW_TYPE = 100;
    public final static int LOADING_SIMPLE_VIEW_TYPE = 101;
    public final static int BACK_AT_HOME_VIEW_TYPE = 200;
    public final static int WEATHER_VIEW_TYPE = 300;
    public final static int ROUTE_VIEW_TYPE = 400;
    public final static int CALORIES_VIEW_TYPE = 500;
    public final static int AD_VIEW_TYPE = 600;

    private final Activity mActivity;

    protected AbstractCard(Activity activity) {
        this.mActivity = activity;
    }

    public abstract int getViewType();

    public abstract boolean canDismiss();

    /**
     * Returns the preference key that is used to know is a card should be displayed or
     * directly cached
     * @return The preference key to use
     */
    public String getPreferenceKey() {
        String prefKey = null;
        switch (getViewType()) {
            case AbstractCard.WEATHER_VIEW_TYPE:
                prefKey = mActivity.getString(R.string.pref_card_weather_displayed);
                break;
            case AbstractCard.ROUTE_VIEW_TYPE:
                prefKey = mActivity.getString(R.string.pref_card_route_displayed);
                break;
            case AbstractCard.CALORIES_VIEW_TYPE:
                prefKey = mActivity.getString(R.string.pref_card_calories_displayed);
                break;
        }
        return prefKey;
    }

}
