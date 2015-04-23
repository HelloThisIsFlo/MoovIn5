package com.shockn745.workoutmotivationaltool.recyclerview.cards;

/**
 * Card to display ads, not dismissable
 */
public class CardAd extends CardSimple implements CardInterface{

    public CardAd(String mText) {
        super(mText);
    }

    @Override
    public boolean canDismiss() {
        return false;
    }
}
