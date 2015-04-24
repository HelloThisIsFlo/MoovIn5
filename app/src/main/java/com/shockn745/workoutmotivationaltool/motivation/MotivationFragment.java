package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.background.BackgroundController;
import com.shockn745.workoutmotivationaltool.motivation.background.ConnectionListener;

import java.util.Date;

/**
 * Fragment of MotivationActivity
 * see the {@link MotivationActivity} class
 */
public class MotivationFragment extends Fragment
        implements BackgroundController.BackgroundControllerListener{

    private static final String LOG_TAG = MotivationFragment.class.getSimpleName();

    // Runnable to display a dialog when the location is unavailable
    private Runnable mExpiredRunnable;

    private BackgroundController mBackgroundController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the backgroundController
        mBackgroundController = new BackgroundController(getActivity(), this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_motivation, container, false);

        mBackgroundController.handleResult(BackgroundController.LOC_INIT);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Connect the GoogleApiClient
        mBackgroundController.handleResult(BackgroundController.CONN_REQ);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Clear resources
        mBackgroundController.handleResult(BackgroundController.CLEAR_RESOURCES);
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
                mBackgroundController.handleResult(BackgroundController.CONN_REQ);
            }
        }
    }


    ///////////////////////////////////////////
    // BackgroundControllerListener Listener //
    ///////////////////////////////////////////

    /**
     * Called when the "back at home time" is available
     * Update the UI
     * @param backAtHome Time back at home
     */
    @Override
    public void onBackAtHomeTimeRetrieved(Date backAtHome) {
        // TODO Use card
        TextView textView = (TextView) getActivity()
                .findViewById(R.id.motivation_text_view);

        textView.setText(
                DateFormat
                        .getTimeFormat(MotivationFragment.this.getActivity())
                        .format(backAtHome)
        );
    }

}