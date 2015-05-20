package com.shockn745.moovin5.motivation.add_card_menu;

/**
 * Callback used to allow the fragment to request animations on the FAB.
 *
 * @author Florian Kempenich
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

    /**
     * @return True if FAB is hidden
     */
    boolean isFABHidden();
}