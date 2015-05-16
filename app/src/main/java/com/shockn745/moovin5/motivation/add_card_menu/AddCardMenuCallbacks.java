package com.shockn745.moovin5.motivation.add_card_menu;

/**
 * Callback used to allow the fragment to request animations on the AddCardMenu.
 * @author Florian Kempenich
 */
public interface AddCardMenuCallbacks {

    /**
     * Reveal the addCardMenu
     */
    void revealAddCardMenu();

    /**
     * Hide the addCardMenu
     */
    void hideAddCardMenu();
}
