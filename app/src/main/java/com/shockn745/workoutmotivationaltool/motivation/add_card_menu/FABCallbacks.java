package com.shockn745.workoutmotivationaltool.motivation.add_card_menu;

/**
 * Callback used to allow the fragment to request animations on the FAB.
 */
public interface FABCallbacks {

    /**
     * Reveal the FAB. Is to be used once!
     */
    void revealFAB();

    /**
     * Hide the FAB (when scrolling up)
     */
    void hideFAB();

    /**
     * Hide the FAB (when scrolling down and toolbar fully visible)
     */
    void unHideFAB();
}