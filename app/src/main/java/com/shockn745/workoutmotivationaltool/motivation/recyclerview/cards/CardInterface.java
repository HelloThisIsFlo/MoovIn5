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
    public final static int WEATHER_VIEW_TYPE = 300;
    public final static int ROUTE_VIEW_TYPE = 400;
    public final static int CALORIES_VIEW_TYPE = 500;
    public final static int AD_VIEW_TYPE = 600;


    public int getViewType();

    public boolean canDismiss();

}
