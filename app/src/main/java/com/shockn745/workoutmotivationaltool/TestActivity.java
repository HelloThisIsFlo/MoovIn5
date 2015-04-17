package com.shockn745.workoutmotivationaltool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.shockn745.workoutmotivationaltool.motivation.FetchTransitTask;


public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            // Build fake data for test purposes
            LatLng[] coord = new LatLng[2];
            coord[0] = new LatLng(52.48969964750192, 13.381244726479053);
            coord[1] = new LatLng(52.461685, 13.419890);


            FetchTransitTask fetchTransitTask = new FetchTransitTask();
            fetchTransitTask.execute(coord);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test, container, false);
            return rootView;
        }

    }
}
