package com.shockn745.workoutmotivationaltool.recyclerview.cards;

/**
 * Created by Shock on 21.04.15.
 */
public class CardSimple implements CardInterface{

    private String simpleText;

    public CardSimple(String mText) {
        this.simpleText = mText;
    }

    @Override
    public int getViewType() {
        return CardInterface.SIMPLE_VIEW_TYPE;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }
}
