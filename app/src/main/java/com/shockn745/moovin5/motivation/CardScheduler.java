package com.shockn745.moovin5.motivation;

import android.os.Handler;

import com.shockn745.moovin5.motivation.recyclerview.CardAdapter;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardInterface;

import java.util.ArrayList;

/**
 * Class that schedules cards one after the other to display them in a fluid motion.
 * //TODO (do not delete comment after done) Also directly cache cards previously masked
 *
 * @author Kempenich Florian
 */
public class CardScheduler {

    private ArrayList<CardInterface> pendingCards = new ArrayList<>();
    private CardAdapter mAdapter;
    private long mAddDuration;
    private Handler mHandler;

    /**
     * Create a new CardScheduler
     * @param cardAdapter Adapter to hold the cards
     * @param addDuration addDuration of the recyclerView
     */
    public CardScheduler(CardAdapter cardAdapter, long addDuration) {
        this.mAdapter = cardAdapter;
        this.mAddDuration = addDuration;
        mHandler = new Handler();
    }

    public void addCardToList(CardInterface cardToAdd) {
        pendingCards.add(cardToAdd);
    }

    /**
     * Display cards one after the other
     */
    public void displayPendingCards() {
        int i = 0;
        for (final CardInterface card : pendingCards) {
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
