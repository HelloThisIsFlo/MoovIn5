package com.shockn745.workoutmotivationaltool;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 * @author Florian Kempenich
 */
public class MotivationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MotivationFragment())
                    .commit();
        }
    }

    /**
     * Fragment of MotivationActivity
     * see the {@link com.shockn745.workoutmotivationaltool.MotivationActivity} class
     */
    public static class MotivationFragment extends Fragment implements LocationListener{

        private static final String LOG_TAG = MotivationFragment.class.getSimpleName();

        // Client used to communicate with the Google API for the location
        private GoogleApiClient mGoogleApiClient;

        // Location request used to query the location of the user
        private LocationRequest mLocationRequest;

        public MotivationFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            buildGoogleApiClient();

            // TODO check if we can get a callback on expiration, to notify fail to the user
            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setInterval(1000)
                    .setFastestInterval(500);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_motivation, container, false);
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            // Connect the GoogleApiClient
            mGoogleApiClient.connect();
        }

        @Override
        public void onPause() {
            super.onPause();

            // Remove the location updates
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            // Disconnect the GoogleApiClient
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        /**
         * In the case of this fragment, this function will be called if the connection result
         * started the resolution.
         * After the end of the resolution this function will be called when the activity & fragment
         * resume
         * @param requestCode Code passed to identify that the activity result is from a connection
         *                    resolution
         * @param resultCode Ok if == RESULT_OK
         * @param data Not used
         */
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == ConnectionListener.REQUEST_RESOLVE_ERROR) {
                if (resultCode == RESULT_OK) {
                    // Try to connect after resolution
                    if (!mGoogleApiClient.isConnecting() &&
                            !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                }
            }
        }

        /**
         * Initialize the mGoogleApiClient used to communicate with the Google API for the location
         */
        protected synchronized void buildGoogleApiClient() {
            // Initialize the GoogleApiClient. This fragment is set as the connection listener
            // for succeeded and failed connections
            ConnectionListener listener = new ConnectionListener();
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(listener)
                    .addOnConnectionFailedListener(listener)
                    .addApi(LocationServices.API)
                    .build();
        }

        /**
         * Handle the newly generated location
         * @param location Location to be handled
         */
        private void handleNewLocation(Location location) {
            Log.d(LOG_TAG, location.toString());
        }

        /**
         * Callback called when a new location is available
         * @param location New location
         */
        @Override
        public void onLocationChanged(Location location) {
            handleNewLocation(location);
        }

        /**
         * Connection listener used for the communication with the Google API.
         * For more information check the "See also" section.
         * @see <a href="http://developer.android.com/google/auth/api-client.html">
         *     Accessing Google APIs</a>
         */
        private class ConnectionListener
                implements GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener {

            // Request code to use when launching the resolution activity
            // Checked in MotivationFragment.OnActivityResult(...)
            private static final int REQUEST_RESOLVE_ERROR = 1001;

            /**
             * Callback called when the connection to the location API succeeded
             * @param bundle
             */
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(LOG_TAG, "Location service connected");

                // Request location
                LocationServices
                        .FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient,
                                mLocationRequest,
                                MotivationFragment.this);
            }

            /**
             * Callback called when the connection to the location API is suspended
             * @param i
             */
            @Override
            public void onConnectionSuspended(int i) {
                Log.i(LOG_TAG, "Location service suspended! Please reconnect");
            }

            /**
             * Callback called when the connection to the location API fails
             * @param connectionResult
             */
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                // Check if the connection result is able to request the user to take immediate
                // action to resolve the error
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult
                                .startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO implement dialogError
                    // See : http://developer.android.com/google/auth/api-client.html
                    Log.i(LOG_TAG, "Location services connection failed with code "
                            + connectionResult.getErrorCode());
                }

            }
        }

    }
}
