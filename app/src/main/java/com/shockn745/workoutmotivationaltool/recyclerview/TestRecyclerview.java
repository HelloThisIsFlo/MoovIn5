package com.shockn745.workoutmotivationaltool.recyclerview;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardInterface;

import java.util.ArrayList;


public class TestRecyclerview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recyclerview);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new TestRecyclerviewFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TestRecyclerviewFragment extends Fragment {

        private static final String LOG_TAG = TestRecyclerviewFragment.class.getSimpleName();

        private RecyclerView mRecyclerView;
        private TestAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        public TestRecyclerviewFragment() {
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


            // Set recyclerView
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            // Notify the recyclerView that its size won't change (better perfs)
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            // Set the adapter with empty dataset
            ArrayList<CardInterface> testDataset= new ArrayList<>();

            mAdapter = new TestAdapter(testDataset);
            mRecyclerView.setAdapter(mAdapter);


            new DefaultItemAnimator();

            return rootView;
        }
    }
}
