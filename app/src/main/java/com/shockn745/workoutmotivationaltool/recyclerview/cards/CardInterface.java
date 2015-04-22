package com.shockn745.workoutmotivationaltool.recyclerview.cards;

/**
 * Created by Shock on 21.04.15.
 */
public interface CardInterface {

    public final static int CONTACT_VIEW_TYPE = 17;
    public final static int SIMPLE_VIEW_TYPE = 132;

    public int getViewType();

    public boolean canDismiss();

}
