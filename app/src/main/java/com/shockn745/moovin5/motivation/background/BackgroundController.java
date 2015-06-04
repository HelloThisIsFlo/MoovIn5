package com.shockn745.moovin5.motivation.background;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.settings.PreferencesUtils;

/**
 * Class used to factor all the functions and parameters related to handling
 * background processing.<br>
 * The following scenarios are possible :<br>
 * - Location query initiation <br>
 * - Location succeeded<br>
 * - Location failed<br>
 * - Transit time retrieved<br>
 * - . . .<br><br>
 *
 * @author Florian Kempenich
 */
public class BackgroundController implements
        FetchTransitTask.OnBackAtHomeTimeRetrievedListener,
        FetchWeatherTask.OnWeatherInfoRetrievedListener,
        LocationListener {

    public static class BackgroundProcessResult {
        public final FetchTransitTask.TransitInfos mTransitInfos;
        public final FetchWeatherTask.WeatherInfos mWeatherInfos;

        public BackgroundProcessResult(
                FetchTransitTask.TransitInfos transitInfos,
                FetchWeatherTask.WeatherInfos weatherInfos) {
            this.mTransitInfos = transitInfos;
            this.mWeatherInfos = weatherInfos;
        }
    }

    public interface BackgroundControllerListener {

        int ERROR_LOCATION_FAIL = 10;

        int ERROR_TRANSIT_FAIL = 20;
        int ERROR_TRANSIT_CONNECTION_FAIL = 21;
        int ERROR_TRANSIT_NO_ROUTES = 22;

        int ERROR_WEATHER_FAIL = 30;
        int ERROR_WEATHER_CONNECTION_FAIL = 31;

        int ERROR_GYM_NOT_INITIALIZED = 100;


        /**
         * Called when the application exits the loading state
         */
        void onLoadingStateFinished();

        /**
         * Called when the application enters the loading state
         */
        void onLoadingStateInitiated();

        /**
         * Called when all backgroung processing is finished
         * @param result Result of the background processing
         */
        void onBackgroundProcessDone(BackgroundProcessResult result);

        /**
         * Called when there is an error in the processing
         * @param errorCode See static fields
         */
        void onBackgroundProcessError(int errorCode);

    }

    // Client used to communicate with the Google API for the location
    private final GoogleApiClient mGoogleApiClient;

    // Location request parameters
    private static int LOC_REQ_INTERVAL;
    private static int LOC_REQ_FASTEST_INTERVAL;
    private static int LOC_REQ_EXPIRATION;

    // Constant fields to pass to handleResult(...)
    public static final int INIT_LOADING = 0;

    public static final int CONN_OK = 11;

    private static final int LOC_OK = 20;
    private static final int LOC_FAIL = 21;

    private static final int FETCH_TRANSIT_DONE = 30;
    private static final int FETCH_TRANSIT_ERROR = 31;
    private static final int FETCH_TRANSIT_CONNECTION_ERROR = 32;
    private static final int FETCH_TRANSIT_NO_ROUTES = 33;

    private static final int FETCH_WEATHER_DONE = 40;
    private static final int FETCH_WEATHER_ERROR = 41;
    private static final int FETCH_WEATHER_CONNECTION_ERROR = 42;

    public static final int CLEAR_RESOURCES = 100;
    private static final int BG_PROCESS_SUCCESS = 101;
    private static final int GYM_NOT_INIT = 102;


    // Results of background tasks
    private FetchTransitTask.TransitInfos mTransitInfos = null;
    private FetchWeatherTask.WeatherInfos mWeatherInfos = null;


    private Location mLocation; // Current location of the user
    private Runnable mExpiredRunnable; // Expiration timer for when the location is not available

    private final Handler mHandler;

    private final Activity mActivity;
    private final BackgroundControllerListener mListener;

    private boolean mFetchingLocation = false;
    private boolean mFetchLocationDone = false;
    private boolean mFetchingTransit = false;
    private boolean mFetchTransitDone = false;
    private boolean mFetchingWeather = false;
    private boolean mFetchWeatherDone = false;

    private boolean mFetchTransitSecondTry = false;
    private boolean mFetchWeatherSecondTry = false;

    private final boolean mInHomeMode;


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

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        mInHomeMode = prefs.getBoolean(mActivity.getString(R.string.pref_home_mode_key), false);

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
                // If called on fetch tasks second try, do not notify listener
                if (!mFetchTransitSecondTry && !mFetchWeatherSecondTry) {
                    mListener.onLoadingStateInitiated();
                }
                if (!mInHomeMode) {
                    // Not in home mode : normal behavior
                    if (!mFetchingLocation && !mFetchLocationDone) {
                        connectToGoogleApi();
                    }
                } else {
                    // In home mode : skip location retrieval
                    handleResult(LOC_OK);
                }
                if (!mFetchWeatherDone && !mFetchingWeather) {
                    startFetchWeatherTaskWithGymLocation();
                    mFetchingWeather = true;
                }
                break;

            case CONN_OK:
                if (!mFetchLocationDone && !mFetchingLocation) {
                    requestLocationUpdates();
                    startExpirationTimer();
                    mFetchingLocation = true;
                }
                break;

            case LOC_OK:
                mFetchingLocation = false;
                mFetchLocationDone = true;
                cancelExpirationTimer();
                if (!mFetchTransitDone && !mFetchingTransit) {
                    startFetchTransitTaskWithNewLocation();
                    mFetchingTransit = true;
                }
                break;

            case LOC_FAIL:
                mFetchingLocation = false;
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_LOCATION_FAIL
                );
                break;

            case FETCH_TRANSIT_DONE:
                mFetchingTransit = false;
                mFetchTransitDone = true;
                sendResultToListenerIfAllProcessingDone();
                break;

            case FETCH_TRANSIT_ERROR:
                mFetchingTransit = false;
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_TRANSIT_FAIL
                );
                break;

            case FETCH_TRANSIT_CONNECTION_ERROR:
                mFetchingTransit = false;
                if (mFetchTransitSecondTry) {
                    mListener.onLoadingStateFinished();
                    mListener.onBackgroundProcessError(
                            BackgroundControllerListener.ERROR_TRANSIT_CONNECTION_FAIL
                    );
                } else {
                    // Try one more time
                    mFetchTransitSecondTry = true;
                    handleResult(INIT_LOADING);
                }
                break;

            case FETCH_TRANSIT_NO_ROUTES:
                mFetchingTransit = false;
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_TRANSIT_NO_ROUTES
                );
                break;

            case FETCH_WEATHER_DONE:
                mFetchingWeather = false;
                mFetchWeatherDone = true;
                sendResultToListenerIfAllProcessingDone();
                break;

            case FETCH_WEATHER_ERROR:
                mFetchingWeather = false;
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_WEATHER_FAIL
                );
                break;

            case FETCH_WEATHER_CONNECTION_ERROR:
                mFetchingWeather = false;
                if (mFetchWeatherSecondTry) {
                    mListener.onLoadingStateFinished();
                    mListener.onBackgroundProcessError(
                            BackgroundControllerListener.ERROR_WEATHER_CONNECTION_FAIL
                    );
                } else {
                    // Try one more time
                    mFetchWeatherSecondTry = true;
                    handleResult(INIT_LOADING);
                }
                break;

            case CLEAR_RESOURCES:
                mFetchingLocation = false;
                mFetchingWeather = false;
                mFetchingTransit = false;
                cancelExpirationTimer();
                removeLocationUpdates();
                disconnectFromGoogleApi();
                break;

            case BG_PROCESS_SUCCESS:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessDone(
                        new BackgroundProcessResult(
                                mTransitInfos,
                                mWeatherInfos
                        )
                );
                break;

            case GYM_NOT_INIT:
                mListener.onLoadingStateFinished();
                mListener.onBackgroundProcessError(
                        BackgroundControllerListener.ERROR_GYM_NOT_INITIALIZED
                );
                break;

            default:
                break;
        }
    }



    /**
     * Handle the newly generated location, accessible vie the mLocation field
     * Location can be null if inHomeMode == true
     */
    private void startFetchTransitTaskWithNewLocation() {
        try {
            // Get gym location from preferences
            LatLng gymLoc = PreferencesUtils.getCoordinatesFromPreferences(mActivity);

            // Fetch transit time from Google Directions API
            FetchTransitTask fetchTask = new FetchTransitTask(mActivity, this, mInHomeMode);
            LatLng mLocLatLng = null;
            // Only init if not inHomeMode
            if (!mInHomeMode) {
                mLocLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            }
            fetchTask.execute(mLocLatLng, gymLoc);

        } catch (PreferencesUtils.PreferenceNotInitializedException e) {
            handleResult(GYM_NOT_INIT);
        }
    }


    /**
     * Starts the FetchWeatherTask
     */
    private void startFetchWeatherTaskWithGymLocation() {
        // Retrieve gym location
        try {
            LatLng coordGym = PreferencesUtils.getCoordinatesFromPreferences(mActivity);

            new FetchWeatherTask(mActivity, this).execute(coordGym);

        } catch (PreferencesUtils.PreferenceNotInitializedException e) {
            handleResult(GYM_NOT_INIT);
        }
    }


    /**
     * Check if all AsyncTasks have been successfully executed,
     * if so notify the listener that the result is ready
     */
    private void sendResultToListenerIfAllProcessingDone() {
        // NOTE: Add other type of result (if applicable)
        // Check that all variable have been successfully initialized
        if (mFetchTransitDone && mFetchWeatherDone) {
            handleResult(BG_PROCESS_SUCCESS);
        }
    }


    //////////////////////////////////////////
    // Connect/disconnect to the Google API //
    //////////////////////////////////////////

    /**
     * This function connects to the google API,
     * then once connected the transit infos are retrieved
     * @see {com.shockn745.workoutmotivationaltool.motivation.background.ConnectionListener#onConnected}
     */
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
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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
     * @param transitInfos Transit infos
     * @param resultCode RESULT_OK if OK <br>
     *                   ERROR if error <br>
     *                   NO_ROUTES_ERROR if no routes <br>
     */
    @Override
    public void onBackAtHomeTimeRetrieved(FetchTransitTask.TransitInfos transitInfos,
                                          int resultCode) {
        switch (resultCode) {
            case FetchTransitTask.RESULT_OK:
                mTransitInfos = transitInfos;
                handleResult(BackgroundController.FETCH_TRANSIT_DONE);
                break;

            case FetchTransitTask.CONNECTION_ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_CONNECTION_ERROR);
                break;

            case FetchTransitTask.NO_ROUTES_ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_NO_ROUTES);
                break;

            case FetchTransitTask.ERROR:
                handleResult(BackgroundController.FETCH_TRANSIT_ERROR);
                break;

            default:
        }
    }



    ////////////////////////////////////////
    // OnBackAtHomeTimeRetrieved Listener //
    ////////////////////////////////////////

    /**
     * Callback called when FetchWeatherTask is done
     * @param weatherInfos Current weather at the gym location
     * @param resultCode RESULT_OK if OK <br>
     *                   ERROR if error <br>
     */
    @Override
    public void onWeatherInfoRetrieved(FetchWeatherTask.WeatherInfos weatherInfos, int resultCode) {
        switch (resultCode) {
            case FetchWeatherTask.RESULT_OK:
                mWeatherInfos = weatherInfos;
                handleResult(BackgroundController.FETCH_WEATHER_DONE);
                break;

            case FetchWeatherTask.CONNECTION_ERROR:
                handleResult(BackgroundController.FETCH_WEATHER_CONNECTION_ERROR);
                break;

            case FetchWeatherTask.ERROR:
                handleResult(BackgroundController.FETCH_WEATHER_ERROR);
                break;

            default:
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
