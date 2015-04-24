package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

/**
 * Interface for the cards used in MotivationActivity
 */
public interface CardInterface {

    // TODO remove
    public final static int CONTACT_VIEW_TYPE = 17;
    public final static int SIMPLE_VIEW_TYPE = 132;

    public final static int LOADING_VIEW_TYPE = 100;
    public final static int LOADING_SIMPLE_VIEW_TYPE = 101;
    public final static int BACK_AT_HOME_VIEW_TYPE = 200;


    public int getViewType();

    public boolean canDismiss();

}
