package com.shockn745.workoutmotivationaltool.motivation.background;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.settings.PreferencesUtils;

import java.util.Date;

/**
 * Class used to factor all the functions and parameters related to handling
 * background processing.<br>
 * The following scenarios are possible :<br>
 * - Location query initiation <br>
 * - Location succeeded<br>
 * - Location failed<br>
 * - Transit time retrieved<br>
 * - . . .<br><br>
 * Note : Not related to Android Handler
 */
public class BackgroundController
        implements FetchTransitTask.OnBackAtHomeTimeRetrievedListener, LocationListener {

    private static final String LOG_TAG = BackgroundController.class.getSimpleName();

    // Client used to communicate with the Google API for the location
    private GoogleApiClient mGoogleApiClient;

    // Connection parameters
    private static final int LOC_REQ_INTERVAL = 1000;
    private static final int LOC_REQ_FASTEST_INTERVAL = 500;
    // TODO check if 30 sec is enough
    private static final int LOC_REQ_EXPIRATION = 30000;

    // Constant fields to pass to handleResult(...)
    public static final int CONN_REQ = 10;
    public static final int CONN_OK = 11;
    public static final int LOC_INIT = 20;
    public static final int LOC_OK = 21;
    public static final int LOC_FAIL = 22;
    public static final int FETCH_TRANSIT_DONE = 30;
    public static final int CLEAR_RESOURCES = 100;

    // Progress dialog shown during processing
    private ProgressDialog mProgressDialog;

    private Date mBackAtHomeTime;
    private Location mLocation; // Current location of the user
    private Runnable mExpiredRunnable; // Expiration timer for when the location is not available

    private Handler mHandler;

    private Activity mActivity;
    private BackgroundControllerListener mListener;
//    private Handler mHandler;

    public interface BackgroundControllerListener {

        /**
         * Called when the "back at home time" is available
         * @param backAtHome Time back at home
         */
        public void onBackAtHomeTimeRetrieved(Date backAtHome);

        //TODO add other functions

    }

    public BackgroundController(Activity mActivity, BackgroundControllerListener mListener) {
        this.mActivity = mActivity;
        this.mListener = mListener;
        mHandler = new Handler();

        // Initialize the GoogleApiClient.
        ConnectionListener listener = new ConnectionListener(mActivity, this);
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Handle the result of the processing.
     *
     * @param result Type of result, see constant fields
     */
    public void handleResult(int result) {
        switch (result) {
            case CONN_REQ:
                connectToGoogleApi();
                break;

            case CONN_OK:
                requestLocationUpdates();
                startExpirationTimer();
                break;

            case LOC_INIT:
                showLoadingDialog();
                break;

            case LOC_OK:
                cancelExpirationTimer();
                dismissLoadingDialog();
                handleNewLocation();
                break;

            case LOC_FAIL:
                dismissLoadingDialog();
                showUnableToObtainLocationDialog();
                break;

            case FETCH_TRANSIT_DONE:
                // Update the UI
                mListener.onBackAtHomeTimeRetrieved(mBackAtHomeTime);
                break;

            case CLEAR_RESOURCES:
                cancelExpirationTimer();
                removeLocationUpdates();
                disconnectFromGoogleApi();
                break;

            default:
                Log.d(LOG_TAG, "handleResult : Result type not recognized");
                break;
        }
    }



    /**
     * Handle the newly generated location, accessible vie the mLocation field
     */
    private void handleNewLocation() {
        if (mLocation != null) {
            Log.d(LOG_TAG, mLocation.toString());

            try {
                // Get gym location from preferences
                LatLng gymLoc = PreferencesUtils.getCoordinatesFromPreferences(mActivity);

                // Fetch transit time from Google Directions API
                FetchTransitTask fetchTask = new FetchTransitTask(mActivity, this);
                LatLng mLocLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                fetchTask.execute(mLocLatLng, gymLoc);

            } catch (PreferencesUtils.PreferenceNotInitializedException e) {
                // Dismiss the currently displayed progress dialog
                dismissLoadingDialog();
                // Show error dialog
                showGymNotInitDialog();
            }
        } else {
            Log.d(LOG_TAG, "mLocation == null !");
        }
    }



    //////////////////////////////////////////
    // Connect/disconnect to the Google API //
    //////////////////////////////////////////

    private void connectToGoogleApi() {
        if (!mGoogleApiClient.isConnecting() &&
                !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    private void disconnectFromGoogleApi() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    /////////////////////////////////////
    // Request/remove location updates //
    /////////////////////////////////////

    /**
     * Requests the location updates after a successful connection to the Google API
     */
    private void requestLocationUpdates() {

        // Create the LocationRequest object
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setInterval(LOC_REQ_INTERVAL)
                .setFastestInterval(LOC_REQ_FASTEST_INTERVAL)
                .setExpirationDuration(LOC_REQ_EXPIRATION);

        // Request location
        LocationServices
                .FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient,
                        request,
                        this);
    }

    private void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startExpirationTimer() {
        // Start an expiration timer for when the location is not available
        mExpiredRunnable = new Runnable() {
            @Override
            public void run() {
                handleResult(BackgroundController.LOC_FAIL);
            }
        };
        mHandler.postDelayed(mExpiredRunnable, LOC_REQ_EXPIRATION);
    }

    private void cancelExpirationTimer() {
        mHandler.removeCallbacks(mExpiredRunnable);
    }



    /////////////////////////////////
    // Show/dismiss dialog methods //
    /////////////////////////////////

    /**
     * Show a card that indicates the user that the application is loadoing something. <br>
     * Here what's being loaded is : <br>
     *     - Retrieve current location <br>
     *     - Fetch transit time <br>
     */
    private void showLoadingDialog() {
        // Set up & show the progress dialog
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissLoadingDialog() {
        mProgressDialog.dismiss();
    }

    /**
     * Display a dialog informing the user that the location could not be retrieved, and gives
     * him some hints to resolve the problem<br>
     * The dialog can only be dismissed by a clicking the button, and it finishes the activity
     * afterwards.
     */
    private void showUnableToObtainLocationDialog() {
        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setMessage(mActivity.getResources().getString(R.string.alert_location_message))
                .setPositiveButton(mActivity.getResources().getString(R.string.alert_location_dismiss),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mActivity.finish();
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
     * Display a dialog informing the user that the gym location has not been initialized,
     * and invites him to initialize it.
     * The dialog can only be dismissed by a clicking the button, and it finishes the activity
     * afterwards.
     */
    private void showGymNotInitDialog() {
        // Create the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setMessage(
                        mActivity.getResources().getString(R.string.warning_not_initialized_edit_text)
                )
                .setPositiveButton(
                        mActivity.getResources().getString(R.string.alert_location_dismiss),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mActivity.finish();
                            }
                        }
                ).create();

        // Prevent the dialog from being dismissed, so it can call finish() on the activity
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        // Show the dialog
        dialog.show();
    }



    ////////////////////////////////////////
    // OnBackAtHomeTimeRetrieved Listener //
    ////////////////////////////////////////

    /**
     * Callback called when FetchTransitTask is done
     * @param backAtHome Time back at home
     */
    @Override
    public void onBackAtHomeTimeRetrieved(Date backAtHome) {
        mBackAtHomeTime = backAtHome;
        handleResult(BackgroundController.FETCH_TRANSIT_DONE);
    }



    ///////////////////////
    // Location Listener //
    ///////////////////////

    /**
     * Callback called when a new location is available
     *
     * @param location New location
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        handleResult(BackgroundController.LOC_OK);
    }

}
