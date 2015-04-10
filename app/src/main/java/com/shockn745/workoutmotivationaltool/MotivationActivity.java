package com.shockn745.workoutmotivationaltool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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

        private static final int LOC_REQ_INTERVAL = 1000;
        private static final int LOC_REQ_FASTEST_INTERVAL = 500;
        // TODO check if 20 sec is enough
        private static final int LOC_REQ_EXPIRATION = 20000;

        // Client used to communicate with the Google API for the location
        private GoogleApiClient mGoogleApiClient;

        // Location request used to query the location of the user
        private LocationRequest mLocationRequest;

        private Handler mHandler;

        // Progress dialog shown during processing
        private ProgressDialog mProgressDialog;

        // Runnable to display a dialog when the location is unavailable
        private final Runnable mExpiredRunnable = new Runnable() {
            @Override
            public void run() {
                // TODO Call handleResult(...) instead
                showUnableToObtainLocation();
            }
        };

        public MotivationFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            buildGoogleApiClient();

            mHandler = new Handler();

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setInterval(LOC_REQ_INTERVAL)
                    .setFastestInterval(LOC_REQ_FASTEST_INTERVAL)
                    .setExpirationDuration(LOC_REQ_EXPIRATION);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_motivation, container, false);

            // Set up & show the progress dialog
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

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

            // Stop expiration timer
            mHandler.removeCallbacks(mExpiredRunnable);

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
            // Stop the expiration timer
            mHandler.removeCallbacks(mExpiredRunnable);

            // TODO put in handleResult(...)
            // Dismiss the currently displayed progress dialog
            mProgressDialog.dismiss();

            // TODO Call handleResult(...) instead and save location to member variable
            // Handle location
            handleNewLocation(location);
        }

        private void showUnableToObtainLocation() {
            // TODO put in handleResult(...)
            // Dismiss the currently displayed progress dialog
            mProgressDialog.dismiss();

            // Create the AlertDialog
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(getResources().getString(R.string.alert_location_message))
                    .setPositiveButton(getResources().getString(R.string.alert_location_dismiss),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MotivationFragment.this.getActivity().finish();
                                }
                            })
                    .create();

            // Prevent the dialog from being dismissed, so it can call finish() on the activity
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            // Show the dialog
            dialog.show();
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

                // Set the timer for expiration
                mHandler.postDelayed(mExpiredRunnable, LOC_REQ_EXPIRATION);
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
