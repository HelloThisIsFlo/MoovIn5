package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.background.BackgroundController;
import com.shockn745.workoutmotivationaltool.motivation.background.ConnectionListener;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.CardAdapter;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.CardAnimator;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardBackAtHome;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardInterface;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoading;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoadingSimple;

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

    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
    private ArrayList<CardInterface> mDataset;
    private Handler mHandler;

    private boolean mIsInLoadingState = true;


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
        mBackgroundController = new BackgroundController(getActivity(), this);
        mHandler = new Handler();

        showLoadingCards();

        return rootView;
    }

    private void initRecyclerView() {
        // Set the adapter with empty dataset
        mAdapter = new CardAdapter(new ArrayList<CardInterface>());
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
                        mAdapter
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
        mBackgroundController.handleResult(BackgroundController.CONN_REQ);
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
                mBackgroundController.handleResult(BackgroundController.CONN_REQ);
            }
        }
    }

    private void showLoadingCards() {
        // After 0.5s : Add first card
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsInLoadingState) mAdapter.addCard(
                        // TODO use random sentences from list
                        new CardLoading("Contacting your coach in LA")
                );
            }
        }, 500);

        // After loc_req_expiration / 2 : Add second card
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsInLoadingState) mAdapter.addCard(new CardLoadingSimple("Almost done !"));
            }
        }, getResources().getInteger(R.integer.location_request_expiration) / 2);
    }



    ///////////////////////////////////////////
    // BackgroundControllerListener Listener //
    ///////////////////////////////////////////

    /**
     * Called when the "back at home time" is available
     * Update the UI
     * @param backAtHome Time back at home
     */
    @Override
    public void onBackAtHomeTimeRetrieved(Date backAtHome) {

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
}