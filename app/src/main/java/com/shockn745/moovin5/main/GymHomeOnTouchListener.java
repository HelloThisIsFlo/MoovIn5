package com.shockn745.moovin5.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.shockn745.moovin5.R;

/**
 * Touch listener that reveals the home/gym selection card.
 *
 * @author Kempenich Florian
 */
public class GymHomeOnTouchListener implements View.OnTouchListener {

    private class GymLocationAnimators {
        private ObjectAnimator translationAnimator;
        private ObjectAnimator elevationAnimator;

        public void setTranslationAnimator(ObjectAnimator translationAnimator) {
            this.translationAnimator = translationAnimator;
        }

        public void setElevationAnimator(ObjectAnimator elevationAnimator) {
            this.elevationAnimator = elevationAnimator;
        }

        public ObjectAnimator getTranslationAnimator() {
            return translationAnimator;
        }

        public ObjectAnimator getElevationAnimator() {
            return elevationAnimator;
        }
    }

    private final CardView mHomeCard;
    private final CardView mGymCard;
    private final CardView mGymLocationCard;
    private final float mElevation;
    private final int mDuration;
    private final float mGymLocTranslation;
    private final float mGymLocElevation;


    private final static int TRANSLATION_VALUE_HIDDEN = -10;

    public GymHomeOnTouchListener(CardView homeCard,
                                  CardView gymCard,
                                  CardView gymLocationCard,
                                  int duration) {
        this.mHomeCard = homeCard;
        this.mGymCard = gymCard;
        this.mGymLocationCard = gymLocationCard;
        this.mDuration = duration;

        // Save the initial values
        mGymLocTranslation = gymLocationCard.getTranslationX();
        mGymLocElevation = gymLocationCard.getCardElevation();
        float homeElevation = mHomeCard.getCardElevation();
        float gymElevation = mGymCard.getCardElevation();
        if (homeElevation != gymElevation) {
            throw new IllegalStateException("The 2 cards must have the same elevation! ");
        } else {
            mElevation = homeElevation;
        }

        // Hide the gym location card
        mGymLocationCard.setTranslationX(TRANSLATION_VALUE_HIDDEN);
        mGymLocationCard.setCardElevation(0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (v.getId()) {
            case R.id.main_home_card_view:
                revealHomeGymCard(mGymCard, mHomeCard, x, y, true);
                break;
            case R.id.main_gym_card_view:
                revealHomeGymCard(mHomeCard, mGymCard, x, y, false);
                break;
            default:
                throw new IllegalStateException("View not supported in this listenener!");
        }
        return true;
    }

    /**
     * Reveal one card and hides the other
     * @param toReveal CardView to reveal
     * @param toHide Cardview to hide
     * @param touchX X coordinate of the point of touch
     * @param touchY X coordinate of the point of touch
     */
    private void revealHomeGymCard(CardView toReveal,
                                   final CardView toHide,
                                   int touchX,
                                   int touchY,
                                   boolean popCard) {

        // Process the final radius
        int diffX;
        int diffY;
        int width = toReveal.getWidth();
        int height = toReveal.getHeight();
        if (touchX < width / 2) {
            // First half
            if (touchY < height / 2) {
                // Second quadrant
                diffX = width - touchX;
                diffY = height - touchY;
            } else {
                // Third quadrant
                diffX = width - touchX;
                diffY = touchY;
            }
        } else {
            // Second half
            if (touchY < height / 2) {
                // First quadrant
                diffX = touchX;
                diffY = height - touchY;
            } else {
                // Fourth quadrant
                diffX = touchX;
                diffY = touchY;
            }
        }
        //noinspection SuspiciousNameCombination
        int radius = (int) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

        Animator revealCardMenuAnim = ViewAnimationUtils
                .createCircularReveal(toReveal, touchX, touchY, 0, radius)
                .setDuration(mDuration);

        revealCardMenuAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                toHide.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        ObjectAnimator lowerElevation = ObjectAnimator
                .ofFloat(toHide, "cardElevation", 0)
                .setDuration(mDuration/2);

        toReveal.setVisibility(View.VISIBLE);
        toReveal.setCardElevation(mElevation);

        // Get the Object animator for the gym location card
        GymLocationAnimators gymLocationAnimators = popGymLocationCard(mGymLocationCard, popCard);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(revealCardMenuAnim).with(lowerElevation);
        if (popCard) {
            animatorSet
                    .play(gymLocationAnimators.getTranslationAnimator())
                    .after(revealCardMenuAnim);
            animatorSet
                    .play(gymLocationAnimators.getElevationAnimator())
                    .after(revealCardMenuAnim);
        } else {
            animatorSet
                    .play(gymLocationAnimators.getTranslationAnimator())
                    .after(revealCardMenuAnim);
            animatorSet
                    .play(gymLocationAnimators.getElevationAnimator())
                    .with(revealCardMenuAnim);
        }
        animatorSet.start();
    }

    /**
     * Create an ObjectAnimator that either pops or hides the gym location card
     *
     * @param gymLocationCard Card to animate
     * @param popCard true if card is to be displayed
     * @return ObjectAnimator to animate the view
     */
    private GymLocationAnimators popGymLocationCard(CardView gymLocationCard, boolean popCard) {

        GymLocationAnimators animators = new GymLocationAnimators();

        if (popCard) {
            // Display card
            // Translate card
            animators.setTranslationAnimator(
                    ObjectAnimator.ofFloat(
                            gymLocationCard,
                            "translationX",
                            TRANSLATION_VALUE_HIDDEN,
                            mGymLocTranslation
                    ).setDuration(mDuration)
            );

            // Increase elevation
            animators.setElevationAnimator(
                    ObjectAnimator.ofFloat(
                            gymLocationCard,
                            "cardElevation",
                            0,
                            mGymLocElevation
                    ).setDuration(0)
            );
        } else {
            // Hide card
            // Translate card
            animators.setTranslationAnimator(
                    ObjectAnimator.ofFloat(
                            gymLocationCard,
                            "translationX",
                            mGymLocTranslation,
                            TRANSLATION_VALUE_HIDDEN
                    ).setDuration(mDuration)
            );

            // Lower elevation
            animators.setElevationAnimator(
                    ObjectAnimator.ofFloat(
                            gymLocationCard,
                            "cardElevation",
                            mGymLocElevation,
                            0
                    ).setDuration(0)
            );
        }

        return animators;
    }
}
