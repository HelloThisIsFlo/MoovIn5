package com.shockn745.moovin5.motivation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.recyclerview.CardAdapter;
import com.shockn745.moovin5.motivation.recyclerview.cards.AbstractCard;

import java.util.ArrayList;

/**
 * Class that schedules cards one after the other to display them in a fluid motion.
 * Also directly cache cards previously masked
 *
 * @author Kempenich Florian
 */
public class CardScheduler {

    private ArrayList<AbstractCard> pendingCards = new ArrayList<>();
    private CardAdapter mAdapter;
    private long mAddDuration;
    private Handler mHandler;
    private Activity mActivity;

    /**
     * Create a new CardScheduler
     * @param cardAdapter Adapter to hold the cards
     * @param addDuration addDuration of the recyclerView
     */
    public CardScheduler(Activity activity, CardAdapter cardAdapter, long addDuration) {
        this.mActivity = activity;
        this.mAdapter = cardAdapter;
        this.mAddDuration = addDuration;
        mHandler = new Handler();

        //Init the preferences if not already set
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        if (!prefs.contains(mActivity.getString(R.string.pref_card_weather_displayed))) {
            // Init
            prefs.edit().putBoolean(
                    mActivity.getString(R.string.pref_card_weather_displayed),
                    true
            ).apply();
        }
        if (!prefs.contains(mActivity.getString(R.string.pref_card_route_displayed))) {
            // Init
            prefs.edit().putBoolean(
                    mActivity.getString(R.string.pref_card_route_displayed),
                    true
            ).apply();
        }
        if (!prefs.contains(mActivity.getString(R.string.pref_card_calories_displayed))) {
            // Init
            prefs.edit().putBoolean(
                    mActivity.getString(R.string.pref_card_calories_displayed),
                    true
            ).apply();
        }
    }

    public void addCardToList(AbstractCard cardToAdd) {
        if (cardToAdd.canDismiss()) {
            // Check if card has been previously removed
            boolean addCard = true;
            String prefKey = cardToAdd.getPreferenceKey();
            if (prefKey != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                addCard = prefs.getBoolean(prefKey, true);
            }
            if (addCard) {
                pendingCards.add(cardToAdd);
            } else {
                mAdapter.addCardToCache(cardToAdd);
            }
        } else {
            pendingCards.add(cardToAdd);
        }
    }

    /**
     * Display cards one after the other
     */
    public void displayPendingCards() {
        int i = 0;
        for (final AbstractCard card : pendingCards) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(card);
                }
            }, mAddDuration * i);

            i++;
        }
        pendingCards.clear();
    }
}
