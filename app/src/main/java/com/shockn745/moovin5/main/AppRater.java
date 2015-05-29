package com.shockn745.moovin5.main;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.shockn745.moovin5.R;

/**
 * Class to that promps the user to rate the app after a certain amount of days
 *
 * @author Kempenich Florian
 */
public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 5;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    private final static String PREF_FILE_NAME = "apprater";
    private final static String PREF_DONT_SHOW_KEY = "dontshowagain";
    private final static String PREF_LAUNCH_COUNT_KEY = "launch_count";
    private final static String PREF_FIRST_LAUNCH_KEY = "first_launch";

    /**
     * Prompt the user to rate the app if conditions are fulfilled
     * Conditions : Days from first launch & Launch count
     *
     * @param mContext Context to access resources
     */
    public static void app_launched(Context mContext) {

        SharedPreferences prefs =
                mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        // Check if dialog has already been dismissed
        if (prefs.getBoolean(PREF_DONT_SHOW_KEY, false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong(PREF_LAUNCH_COUNT_KEY, 0) + 1;
        editor.putLong(PREF_LAUNCH_COUNT_KEY, launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong(PREF_FIRST_LAUNCH_KEY, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_FIRST_LAUNCH_KEY, date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.apply();
    }

    private static void showRateDialog(
            final Context mContext,
            final SharedPreferences.Editor editor) {

        final String packageName = mContext.getApplicationContext().getPackageName();
        final String appName = mContext.getString(R.string.app_name);

        // Build the dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(mContext.getString(R.string.app_rater_dialog_title) + " " + appName);
        builder.setMessage(mContext.getString(R.string.app_rater_dialog_message_part_1)
                        + appName
                        + mContext.getString(R.string.app_rater_dialog_message_part_2)
        );

        builder.setPositiveButton(
                R.string.app_rater_dialog_positive,
                new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Launch play store
                            Uri uri = Uri.parse("market://details?id=" + packageName);
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                mContext.startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                // If play store not present : go to website
                                Intent goToWebsite = new Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id="
                                                        + packageName)
                                );
                                mContext.startActivity(goToWebsite);
                            }
                        }
                }
        );

        builder.setNegativeButton(
                R.string.app_rater_dialog_negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editor != null) {
                            editor.putLong(PREF_FIRST_LAUNCH_KEY, System.currentTimeMillis());
                            editor.commit();
                        }
                    }
                }
        );

        builder.setNeutralButton(
                R.string.app_rater_dialog_neutral,
                new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editor != null) {
                                editor.putBoolean(PREF_DONT_SHOW_KEY, true);
                                editor.commit();
                            }
                        }
                }
        );

        // Show the dialog
        builder.show();
    }
}
