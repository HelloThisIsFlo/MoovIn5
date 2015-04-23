package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.background.FetchTransitTask;
import com.shockn745.workoutmotivationaltool.settings.PreferencesUtility;

import java.util.Date;

/**
 * Fragment of MotivationActivity
 * see the {@link MotivationActivity} class
 */
public class MotivationFragment extends Fragment
        implements LocationListener, FetchTransitTask.FetchTransitCallback {

    private static final String LOG_TAG = MotivationFragment.class.getSimpleName();

    private static final int LOC_REQ_INTERVAL = 1000;
    private static final int LOC_REQ_FASTEST_INTERVAL = 500;
    // TODO check if 30 sec is enough
    private static final int LOC_REQ_EXPIRATION = 30000;

    // Client used to communicate with the Google API for the location
    private GoogleApiClient mGoogleApiClient;

    // Location request used to query the location of the user
    private LocationRequest mLocationRequest;

    private Handler mHandler;

    // Progress dialog shown during processing
    private ProgressDialog mProgressDialog;

    // Runnable to display a dialog when the location is unavailable
    private Runnable mExpiredRunnable;

    // ResultHandler handles the result of the processing : loc. fetch, route process, etc...
    private ResultHandler mResultHandler;

    // Current location of the user
    private Location mLocation;

    // Time back at home
    private Date mBackAtHomeTime;

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

        // Initialize the resultHandler
        mResultHandler = new ResultHandler();

        // Initialize the expiration timer
        mExpiredRunnable = new Runnable() {
            @Override
            public void run() {
                mResultHandler.handleResult(ResultHandler.LOC_FAIL);
            }
        };

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
     * Callback called when a new location is available
     *
     * @param location New location
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mResultHandler.handleResult(ResultHandler.LOC_OK);
    }

    /**
     * Handle the newly generated location, accessible vie the mLocation field
     */
    private void handleNewLocation() {
        if (mLocation != null) {
            Log.d(LOG_TAG, mLocation.toString());

            try {
                // Get gym location from preferences
                LatLng gymLoc = PreferencesUtility.getCoordinatesFromPreferences(getActivity());

                // Fetch transit time from Google Directions API
                FetchTransitTask fetchTask = new FetchTransitTask(getActivity(), this);
                LatLng mLocLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                fetchTask.execute(mLocLatLng, gymLoc);

            } catch (PreferencesUtility.PreferenceNotInitializedException e) {
                // Show error dialog
                mResultHandler.handleResult(ResultHandler.GYM_NOT_INIT);
            }
        } else {
            Log.d(LOG_TAG, "mLocation == null !");
        }
    }

    @Override
    public void FetchTransitCallback(Date backAtHome) {
        mBackAtHomeTime = backAtHome;
        mResultHandler.handleResult(ResultHandler.FETCH_TRANSIT_DONE);
    }

    /**
     * Class used to factor all the functions and parameters related to handling the result of
     * the processing.<br>
     * The following scenarios are possible :<br>
     * - Location succeeded<br>
     * - Location failed<br>
     * - . . .<br><br>
     * Note : Not related to Android Handler
     */
    private class ResultHandler {

        // Constant fields to pass to handleResult(...)
        private static final int LOC_OK = 0;
        private static final int LOC_FAIL = 1;
        private static final int GYM_NOT_INIT = 2;
        private static final int FETCH_TRANSIT_DONE = 3;

        /**
         * Handle the result of the processing.
         *
         * @param result Type of result, see constant fields
         */
        private void handleResult(int result) {
            switch (result) {
                case LOC_OK:
                    // Stop the expiration timer
                    mHandler.removeCallbacks(mExpiredRunnable);

                    // Dismiss the currently displayed progress dialog
                    mProgressDialog.dismiss();

                    // Handle location
                    MotivationFragment.this.handleNewLocation();

                    break;
                case LOC_FAIL:
                    // Dismiss the currently displayed progress dialog
                    mProgressDialog.dismiss();

                    showUnableToObtainLocation();
                    break;
                case GYM_NOT_INIT:
                    // Dismiss the currently displayed progress dialog
                    mProgressDialog.dismiss();

                    showGymNotInit();
                    break;
                case FETCH_TRANSIT_DONE:
                    // TODO Use card
                    // Update the UI
                    TextView textView = (TextView) MotivationFragment.this
                            .getActivity()
                            .findViewById(R.id.motivation_text_view);

                    textView.setText(
                        DateFormat
                                .getTimeFormat(MotivationFragment.this.getActivity())
                                .format(mBackAtHomeTime)
                );
                    break;
                default:
                    Log.d(LOG_TAG, "handleResult : Result type not recognized");
                    break;
            }
        }


        /**
         * Display a dialog informing the user that the location could not be retrieved, and gives
         * him some hints to resolve the problem<br>
         * The dialog can only be dismissed by a clicking the button, and it finishes the activity
         * afterwards.
         */
        private void showUnableToObtainLocation() {
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
         * Display a dialof informing the user that the gym location has not been initialized,
         * and invites him to initialize it.
         * The dialog can only be dismissed by a clicking the button, and it finishes the activity
         * afterwards.
         */
        private void showGymNotInit() {
            // Create the AlertDialog
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(
                            getResources().getString(R.string.warning_not_initialized_edit_text)
                    )
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
    }

    /**
     * Connection listener used for the communication with the Google API.
     * For more information check the "See also" section.
     *
     * @see <a href="http://developer.android.com/google/auth/api-client.html">
     * Accessing Google APIs</a>
     */
    private class ConnectionListener
            implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        // Request code to use when launching the resolution activity
        // Checked in MotivationFragment.OnActivityResult(...)
        private static final int REQUEST_RESOLVE_ERROR = 1001;

        /**
         * Callback called when the connection to the location API succeeded
         *
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
         *
         * @param i
         */
        @Override
        public void onConnectionSuspended(int i) {
            Log.i(LOG_TAG, "Location service suspended! Please reconnect");
        }

        /**
         * Callback called when the connection to the location API fails
         *
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