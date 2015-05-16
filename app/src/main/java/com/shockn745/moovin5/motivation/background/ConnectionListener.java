package com.shockn745.moovin5.motivation.background;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Connection listener used for the communication with the Google API.
 * For more information check the "See also" section.
 *
 * @see <a href="http://developer.android.com/google/auth/api-client.html">
 * Accessing Google APIs</a>
 *
 * @author Florian Kempenich
 */
public class ConnectionListener
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = ConnectionListener.class.getSimpleName();

    // Request code to use when launching the resolution activity
    // Checked in MotivationFragment.OnActivityResult(...)
    public static final int REQUEST_RESOLVE_ERROR = 1001;

    final private Activity mActivity;
    final private BackgroundController mBackgroundController;

    public ConnectionListener(Activity mActivity, BackgroundController mBackgroundController) {
        this.mActivity = mActivity;
        this.mBackgroundController = mBackgroundController;
    }

    /**
     * Callback called when the connection to the location API succeeded
     *
     * @param bundle Not used
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "Location service connected");

        mBackgroundController.handleResult(BackgroundController.CONN_OK);
    }

    /**
     * Callback called when the connection to the location API is suspended
     *
     * @param i Not used
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Location service suspended! Please reconnect");
    }

    /**
     * Callback called when the connection to the location API fails
     *
     * @param connectionResult Used to start resolution
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Check if the connection result is able to request the user to take immediate
        // action to resolve the error
        if (connectionResult.hasResolution()) {
            try {
                connectionResult
                        .startResolutionForResult(mActivity, REQUEST_RESOLVE_ERROR);
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