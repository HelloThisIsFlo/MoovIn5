package com.shockn745.workoutmotivationaltool.motivation.background;

import android.app.Activity;
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

    public static class BackgroundProcessResult {
        public final Date mBackAtHomeTime;
        public final FetchWeatherTask.WeatherInfos mWeatherInfos;

        public BackgroundProcessResult(Date backAtHomeTime, FetchWeatherTask.WeatherInfos weatherInfos) {
            this.mBackAtHomeTime = backAtHomeTime;
            this.mWeatherInfos = weatherInfos;
        }
    }

    public interface BackgroundControllerListener {

        public static final int ERROR_LOCATION_FAIL = 10;

        public static final int ERROR_TRANSIT_FAIL = 20;
        public static final int ERROR_TRANSIT_CONNECTION_FAIL = 21;
        public static final int ERROR_TRANSIT_NO_ROUTES = 22;

        public static final int ERROR_GYM_NOT_INITIALIZED = 100;


        /**
         * Called when the application exits the loading state
         */
        public void onLoadingStateFinished();

        /**
         * Called when the application enters the loading state
         */
        public void onLoadingStateInitiated();

        /**
         * Called when all backgroung processing is finished
         * @param result Result of the background processing
         */
        public void onBackgroundProcessDone(BackgroundProcessResult result);
        //TODO add other functions

        /**
         * Called when there is an error in the processing
         * @param errorCode See static fields
         */
        public void onBackgroundProcessError(int errorCode);

    }

    private static final String LOG_TAG = BackgroundController.class.getSimpleName();

    // Client used to communicate with the Google API for the location
    private GoogleApiClient mGoogleApiClient;

    // Location request parameters
    private static int LOC_REQ_INTERVAL;
    private static int LOC_REQ_FASTEST_INTERVAL;
    private static int LOC_REQ_EXPIRATION;

    // Constant fields to pass to handleResult(...)
    public static final int INIT_LOADING = 0;

    public static final int CONN_OK = 11;

    public static final int LOC_OK = 20;
    public static final int LOC_FAIL = 21;

    public static final int FETCH_TRANSIT_DONE = 30;
    public static final int FETCH_TRANSIT_FAIL = 31;
    public static final int FETCH_TRANSIT_CONNECTION_FAIL = 32;
    public static final int FETCH_TRANSIT_NO_ROUTES = 33;

    public static final int CLEAR_RESOURCES = 100;
    public static final int BG_PROCESS_SUCCESS = 101;
    private static final int GYM_NOT_INIT = 102;

    // Results of background tasks
    private Date mBackAtHomeTime = null;
    private FetchWeatherTask.WeatherInfos mWeatherInfos = null;


    private Location mLocation; // Current location of the user
    private Runnable mExpiredRunnable; // Expiration timer for when the location is not available

    private Handler mHandler;

    private Activity mActivity;
    private BackgroundControllerListener mListener;




    public BackgroundController(Activity mActivity, BackgroundControllerListener mListener) {
        this.mActivity = mActivity;
        this.mListener = mListener;
        mHandler = new Handler();

        // Init the parameters for the location request
        LOC_REQ_INTERVAL = mActivity.getResources().getInteger(R.integer.location_request_interval);
        LOC_REQ_FASTEST_INTERVAL =
                mActivity.getResources().getInteger(R.integer.location_request_fastest_interval);
        LOC_REQ_EXPIRATION =
                mActivity.getResources().getInteger(R.integer.location_request_expiration);

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
     * Everything comes back to this controller function.
     * This way is way easier to understand the global workflow
     *
     * @param result Type of result, see constant fields
     */
    public void handleResult(int result) {
        switch (result) {
            case INIT_LOADING:
                mListener.onLoadingStateInitiated();
                connectToGoogleApi();
                break;

            case CONN_OK:
                requestLocationUpdates();
                startExpirationTimer();
                break;

            case LOC_OK:
                cancelExpirationTimer();
                startFetchTransitTaskWithNewLocation();
                break;

            case LOC_FAIL:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_LOCATION_FAIL
                );
                break;

            case FETCH_TRANSIT_DONE:
                // Update the UI
                handleOnBackAtHomeTime(mBackAtHomeTime);
                break;

            case FETCH_TRANSIT_FAIL:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        FETCH_TRANSIT_FAIL
                );
                break;

            case FETCH_TRANSIT_CONNECTION_FAIL:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        FETCH_TRANSIT_CONNECTION_FAIL
                );
                break;

            case FETCH_TRANSIT_NO_ROUTES:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        FETCH_TRANSIT_NO_ROUTES
                );
                break;

            case CLEAR_RESOURCES:
                cancelExpirationTimer();
                removeLocationUpdates();
                disconnectFromGoogleApi();
                break;

            case BG_PROCESS_SUCCESS:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessDone(
                        new BackgroundProcessResult(mBackAtHomeTime, mWeatherInfos)
                );
                break;

            case GYM_NOT_INIT:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_GYM_NOT_INITIALIZED
                );
                break;

            default:
                Log.d(LOG_TAG, "handleResult : Result type not recognized");
                break;
        }
    }



    /**
     * Handle the newly generated location, accessible vie the mLocation field
     */
    private void startFetchTransitTaskWithNewLocation() {
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
                handleResult(GYM_NOT_INIT);
            }
        } else {
            Log.d(LOG_TAG, "mLocation == null !");
        }
    }



    ///////////////////////////////////////////////////////////
    // Methods to handle the results of the background tasks //
    ///////////////////////////////////////////////////////////

    /**
     * Check if all AsyncTasks have been successfully executed,
     * if so notify the listener that the result is ready
     */
    private void sendResultIfAllProcessingDone() {
        // TODO add other type of result (if applicable)
        // Check that all variable have been successfully initialized
        if (mBackAtHomeTime != null && mWeatherInfos != null) {
            handleResult(BG_PROCESS_SUCCESS);
        }
    }

    private void handleOnBackAtHomeTime(Date backAtHomeTime) {
        mBackAtHomeTime = backAtHomeTime;
        sendResultIfAllProcessingDone();
    }

    private void handleWeatherInfos(FetchWeatherTask.WeatherInfos weatherInfos) {
        mWeatherInfos = weatherInfos;
        sendResultIfAllProcessingDone();
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
                .requestLocationUpdates(
                        mGoogleApiClient,
                        request,
                        this
                );
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



    ////////////////////////////////////////
    // OnBackAtHomeTimeRetrieved Listener //
    ////////////////////////////////////////

    /**
     * Callback called when FetchTransitTask is done
     * @param backAtHome Time back at home
     */
    @Override
    public void onBackAtHomeTimeRetrieved(Date backAtHome, int resultCode) {
        switch (resultCode) {
            case FetchTransitTask.RESULT_OK:
                mBackAtHomeTime = backAtHome;
                handleResult(BackgroundController.FETCH_TRANSIT_DONE);
                break;

            case FetchTransitTask.CONNECTION_ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_CONNECTION_FAIL);
                break;

            case FetchTransitTask.NO_ROUTES_ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_NO_ROUTES);
                break;

            case FetchTransitTask.ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_FAIL);
                break;
        }
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
