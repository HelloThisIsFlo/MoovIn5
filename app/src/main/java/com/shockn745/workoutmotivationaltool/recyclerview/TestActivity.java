package com.shockn745.workoutmotivationaltool.recyclerview;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.workoutmotivationaltool.recyclerview.animation.TestAnimator;
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardAd;
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardInterface;

import java.util.ArrayList;


public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recyclerview);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new TestFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TestFragment extends Fragment {

        private static final String LOG_TAG = TestFragment.class.getSimpleName();

        private RecyclerView mRecyclerView;
        private TestAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private TestAnimator mAnimator;
        private ArrayList<CardInterface> mDataset;

        private final Handler mHandler = new Handler();

        public TestFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_test, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_add_card) {
                mAdapter.addCardFromLIFO();
            } else if (item.getItemId() == R.id.action_remove_card) {
                mAdapter.removeCard(Math.max(0, mAdapter.getItemCount() - 2));
            } else {
                Log.d(LOG_TAG, "Error : MenuItem not recognized");
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test_recyclerview, container, false);

            // Get the RecyclerView
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            initRecyclerView();

            createTestScenario();

            return rootView;
        }

        private void initRecyclerView() {
            // Set the adapter with empty dataset
            mDataset= new ArrayList<>();

            mAdapter = new TestAdapter(mDataset);
            mRecyclerView.setAdapter(mAdapter);

            // Set recyclerView
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            // Notify the recyclerView that its size won't change (better perfs)
            mRecyclerView.setHasFixedSize(true);
            mAnimator = new TestAnimator(getActivity(), TestAnimator.STYLE_LOADING);
            mAnimator.setRemoveDuration(500);
            mRecyclerView.setItemAnimator(mAnimator);

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

        /**
         * Add Loading card (simple card here) and then 2s later, add second "wait" (funny) message
         */
        private void createTestScenario() {
            // After 0.5s : Add first card
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardAd("LOADING . . ."));

                }
            }, 500);

            // After 2.5s : Add second card
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addCard(new CardAd("Wait just a bit longer . . . "));
                }
            }, 2500);

            // After 5s : Clear the loading screen and add 3 first cards
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.clearLoadingScreen();

                    // Time schedule
                    long addTimes[] = new long[] {
                            mAnimator.getRemoveDuration(),
                            mAnimator.getRemoveDuration() + mAnimator.getAddDuration(),
                            mAnimator.getRemoveDuration() + mAnimator.getAddDuration() * 2,
                            mAnimator.getRemoveDuration() + mAnimator.getAddDuration() * 3,

                    };

                    // Display first test cards & set the remove animation
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Set remove animation
                            mAnimator.setmAnimationStyle(TestAnimator.STYLE_POST_LOADING);

                            // Display first card
                            mAdapter.addCardFromLIFO();
                        }
                    }, addTimes[0]);

                    // Display the rest of the test cards
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addCardFromLIFO();
                        }
                    }, addTimes[1]);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addCardFromLIFO();
                        }
                    }, addTimes[2]);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addCardFromLIFO();
                        }
                    }, addTimes[3]);

                }
            }, 5000);
        }
    }
}
