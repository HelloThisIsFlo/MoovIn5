package com.shockn745.workoutmotivationaltool.recyclerview;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;


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

        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        public TestRecyclerviewFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test_recyclerview, container, false);

            // Get the RecyclerView
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

            // Set layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // Set the adapter
            String[] testDataset = {
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "coucou",
                    "qsdfsdf",
                    "test"
            };
            mAdapter = new testAdapter(testDataset);
            mRecyclerView.setAdapter(mAdapter);





            new DefaultItemAnimator();

            return rootView;
        }
    }
}
