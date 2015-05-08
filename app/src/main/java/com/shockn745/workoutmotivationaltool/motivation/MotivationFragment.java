package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.background.BackgroundController;
import com.shockn745.workoutmotivationaltool.motivation.background.ConnectionListener;
import com.shockn745.workoutmotivationaltool.motivation.background.FetchWeatherTask;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.CardAdapter;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.CardAnimator;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardAd;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardBackAtHome;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardInterface;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoading;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoadingSimple;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardRoute;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardWeather;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.calories.CardCalories;

import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment of MotivationActivity
 * see the {@link MotivationActivity} class
 */
public class MotivationFragment extends Fragment
        implements BackgroundController.BackgroundControllerListener{

    private static final String LOG_TAG = MotivationFragment.class.getSimpleName();

    private BackgroundController mBackgroundController;
    private ErrorHandler mErrorHandler;

    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
    private ArrayList<CardInterface> mDataset;
    private Handler mHandler;

    private boolean mIsInLoadingState = true;
    private boolean mFirstLoadingCardDisplayed = false;
    private boolean mSecondLoadingCardDisplayed = false;
    private boolean mResultCardsDisplayed = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motivation, container, false);

        // Find views by id
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cards_recycler_view);

        initRecyclerView();
        mErrorHandler = new ErrorHandler();
        mBackgroundController = new BackgroundController(getActivity(), this);
        mHandler = new Handler();

        return rootView;
    }

    private void initRecyclerView() {
        // Set the adapter with empty dataset
        mAdapter = new CardAdapter(new ArrayList<CardInterface>(), getActivity());
        mRecyclerView.setAdapter(mAdapter);

        // Set recyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Notify the recyclerView that its size won't change (better perfs)
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(
                new CardAnimator(getActivity(), CardAnimator.STYLE_LOADING)
        );

        // Set the OnTouchListener
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        mAdapter,
                        getActivity()
                );
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Connect the GoogleApiClient
        // TODO Uncomment for test scenario
//        mBackgroundController.handleResult(BackgroundController.TEST_SCENARIO);
        mBackgroundController.handleResult(BackgroundController.INIT_LOADING);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Clear resources
        mBackgroundController.handleResult(BackgroundController.CLEAR_RESOURCES);
    }

    /**
     * In the case of this fragment, this function will be called if the connection result
     * started the resolution.
     * After the end of the resolution this function will be called when the activity & fragment
     * resume
     *
     * @param requestCode Code passed to identify that the activity result is from a connection
     *                    resolution
     * @param resultCode  Ok if == RESULT_OK
     * @param data        Not used
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConnectionListener.REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                // Try to connect after resolution
                mBackgroundController.handleResult(BackgroundController.INIT_LOADING);
            }
        }
    }

    private void showLoadingCards() {
        // Check loading state at initiation
        if (mIsInLoadingState) {
            // After 0.5s : Add first card
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check loading state at execution
                    if (mIsInLoadingState && !mFirstLoadingCardDisplayed) {
                        mFirstLoadingCardDisplayed = true;
                        // TODO use random sentences from list
                        mAdapter.addCard(new CardLoading("Contacting your coach"));
                    }
                }
            }, 500);

            // After loc_req_expiration / 2 : Add second card
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check loading state at execution
                    if (mIsInLoadingState && !mSecondLoadingCardDisplayed) {
                        mSecondLoadingCardDisplayed = true;
                        mAdapter.addCard(new CardLoadingSimple("Almost done !"));
                    }
                }
            }, getResources().getInteger(R.integer.location_request_expiration) / 2);
        }
    }



    ///////////////////////////////////////////
    // BackgroundControllerListener Listener //
    ///////////////////////////////////////////


    @Override
    public void onLoadingStateInitiated() {
        showLoadingCards();
    }

    /**
     * Called when the application exits the loading state
     */
    @Override
    public void onLoadingStateFinished() {
        if (mIsInLoadingState) {
            // Remove loading card(s)
            mAdapter.clearLoadingScreen();
            ((CardAnimator)mRecyclerView.getItemAnimator())
                    .setAnimationStyle(CardAnimator.STYLE_POST_LOADING);
        }

        mIsInLoadingState = false;
    }

    /**
     * Called when all the background processing is done.
     * @param result Result of the background processing
     */
    @Override
    public void onBackgroundProcessDone(BackgroundController.BackgroundProcessResult result) {
        handleBackAtHomeTime(result.mBackAtHomeTime);
        handleWeatherInfo(result.mWeatherInfos);
        // TODO add other functions to handle the other type of result (if applicable)
    }

    /**
     * Called when there is an error in the background process
     *
     * @param errorCode See interface
     * {@link com.shockn745.workoutmotivationaltool.motivation.background.BackgroundController
     * .BackgroundControllerListener} static fields.
     */
    @Override
    public void onBackgroundProcessError(int errorCode) {
        mErrorHandler.handleError(errorCode);
    }

    ////////////////////////////////////////////////////////////
    // Methods to handle results of the background processing //
    ////////////////////////////////////////////////////////////

    /**
     * Called when the "back at home time" is available
     * Update the UI
     * @param backAtHome Time back at home
     */
    private void handleBackAtHomeTime(Date backAtHome) {
        if (!mResultCardsDisplayed) {
            // Format backAtHome time
            final String formattedBackAtHomeTime = DateFormat
                    .getTimeFormat(MotivationFragment.this.getActivity())
                    .format(backAtHome);

            // Show backAtHome card
            // Wait after animation remove duration
            // to allow the animations from clearLoadingScreen to unfold
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardBackAtHome(
                            "You'll be back at home at : "
                                    + formattedBackAtHomeTime
                    ));
                }
            }, mRecyclerView.getItemAnimator().getRemoveDuration());


            // TODO TEST SCENARIO : REMOVE
            // Time schedule
            long removeDuration = mRecyclerView.getItemAnimator().getRemoveDuration();
            long addDuration = mRecyclerView.getItemAnimator().getAddDuration();
            long addTimes[] = new long[]{
                    removeDuration + addDuration, //Not used here anymore
                    removeDuration + addDuration * 2,
                    removeDuration + addDuration * 3,
                    removeDuration + addDuration * 4,
            };

//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mAdapter.addCard(new CardWeather("Sunny"));
//                }
//            }, addTimes[0]);
            // Display the rest of the test cards
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardRoute("This way"));
                }
            }, addTimes[1]);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardAd("PUB"));
                }
            }, addTimes[2]);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardCalories(getActivity()));
                }
            }, addTimes[3]);

        }
        mResultCardsDisplayed = true;
    }

    private void handleWeatherInfo(final FetchWeatherTask.WeatherInfos weatherInfos) {
        long removeDuration = mRecyclerView.getItemAnimator().getRemoveDuration();
        long addDuration = mRecyclerView.getItemAnimator().getAddDuration();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addCard(new CardWeather(weatherInfos));
            }
        }, removeDuration + addDuration);
    }



    ///////////////////
    // Error handler //
    ///////////////////

    /**
     * Class used to handle all error that could happen during the background processing
     */
    private class ErrorHandler {
        // Boolean to prevent displaying two different dialogs, one on top of the other
        // Eg. Connection error
        private boolean mIsErrorDialogAlreadyDisplayed = false;

        /**
         * Called when there is an error in the background process
         *
         * @param errorCode See interface
         * {@link com.shockn745.workoutmotivationaltool.motivation.background.BackgroundController
         * .BackgroundControllerListener} static fields.
         */
        private void handleError(int errorCode) {
            switch (errorCode) {
                case ERROR_LOCATION_FAIL:
                    showUnableToObtainLocationDialog();
                    break;

                case ERROR_TRANSIT_FAIL:
                    showFail();
                    break;
                case ERROR_TRANSIT_CONNECTION_FAIL:
                    showConnectionFail();
                    break;
                case ERROR_TRANSIT_NO_ROUTES:
                    showTransitNoRoutes();
                    break;

                case ERROR_WEATHER_FAIL:
                    showFail();
                    break;

                case ERROR_WEATHER_CONNECTION_FAIL:
                    showConnectionFail();
                    break;

                case ERROR_GYM_NOT_INITIALIZED:
                    // Shouldn't happen, but just in case.
                    showGymNotInitDialog();
                    break;

                default:
                    Log.d(LOG_TAG, "Unknown error !");
            }
        }


        ///////////////////////////////////////
        // Show/dismiss dialog/cards methods //
        ///////////////////////////////////////

        /**
         * Display a dialog informing the user that the location could not be retrieved, and gives
         * him some hints to resolve the problem<br>
         * The dialog can only be dismissed by a clicking the button, and it finishes the activity
         * afterwards.
         */
        private void showUnableToObtainLocationDialog() {
            showErrorDialog(
                    getActivity().getResources().getString(R.string.alert_location_fail)
            );
        }

        /**
         * Display a dialog informing the user that the gym location has not been initialized,
         * and invites him to initialize it.
         * The dialog can only be dismissed by a clicking the button, and it finishes the activity
         * afterwards.
         */
        private void showGymNotInitDialog() {
            showErrorDialog(
                    getActivity().getResources().getString(R.string.warning_not_initialized_edit_text)
            );
        }

        private void showFail() {
            showErrorDialog(
                    getActivity().getResources().getString(R.string.alert_fail)
            );
        }

        private void showConnectionFail() {
            showErrorDialog(
                    getActivity().getResources().getString(R.string.alert_connection_fail)
            );
        }

        private void showTransitNoRoutes() {
            showErrorDialog(
                    getActivity().getResources().getString(R.string.alert_transit_no_routes)
            );
        }



        ///////////////////
        // Utility method//
        ///////////////////

        /**
         * Shows an AlertDialog with a custom message.
         * The AlertDialog can only be dismissed by a clicking the button, and it finishes the activity
         * afterwards.
         *
         * @param message Message to display
         */
        private void showErrorDialog(String message) {
            if (!mIsErrorDialogAlreadyDisplayed) {
                mIsErrorDialogAlreadyDisplayed = true;
                // Create the AlertDialog
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton(
                                getActivity().getResources().getString(R.string.alert_dismiss),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                }
                        ).create();

                // Prevent the dialog from being dismissed, so it can call finish() on the activity
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                // Show the dialog
                dialog.show();
            }
        }

    }
}
