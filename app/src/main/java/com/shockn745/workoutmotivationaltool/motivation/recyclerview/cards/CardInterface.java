package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

/**
 * Interface for the cards used in MotivationActivity
 */
public interface CardInterface {

    int LOADING_VIEW_TYPE = 100;
    int LOADING_SIMPLE_VIEW_TYPE = 101;
    int BACK_AT_HOME_VIEW_TYPE = 200;
    int WEATHER_VIEW_TYPE = 300;
    int ROUTE_VIEW_TYPE = 400;
    int CALORIES_VIEW_TYPE = 500;
    int AD_VIEW_TYPE = 600;


    int getViewType();

    boolean canDismiss();

}
