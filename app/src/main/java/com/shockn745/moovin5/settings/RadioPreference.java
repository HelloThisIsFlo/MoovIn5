package com.shockn745.moovin5.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shockn745.moovin5.R;

/**
 * Class that defines a custom DialogPreferences used to display a radioGroup in preferences
 *
 * @author Kempenich Florian
 */
public class RadioPreference extends DialogPreference {

    private RadioGroup mRadioGroup;
    private boolean mIsCelsiusUnit;

    @SuppressWarnings("unused")
    public RadioPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public RadioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    }

    @Override
    protected View onCreateDialogView() {

        // Create the layout parameters for the radioGroup
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = (int) getContext()
                .getResources()
                .getDimension(R.dimen.radio_group_top_margin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart((int) getContext()
                            .getResources()
                            .getDimension(R.dimen.radio_group_start_margin)
            );
        }
        layoutParams.leftMargin = (int) getContext()
                .getResources()
                .getDimension(R.dimen.radio_group_start_margin);

        // Create the picker and set the layout parameters
        mRadioGroup = new RadioGroup(getContext());
        mRadioGroup.setLayoutParams(layoutParams);
        mRadioGroup.setOrientation(RadioGroup.VERTICAL);

        // Add the two radio buttons
        RadioButton celsiusButton = new RadioButton(getContext());
        RadioButton fahrenheitButton = new RadioButton(getContext());
        celsiusButton.setText(getContext().getString(R.string.pref_is_celsius_celsius));
        fahrenheitButton.setText(getContext().getString(R.string.pref_is_celsius_fahrenheit));
        celsiusButton.setTextSize(
                getContext()
                        .getResources()
                        .getDimension(R.dimen.radio_button_text_size)
        );
        fahrenheitButton.setTextSize(
                getContext()
                        .getResources()
                        .getDimension(R.dimen.radio_button_text_size)
        );

        celsiusButton.setId(R.id.pref_unit_celsius_radio_button);
        fahrenheitButton.setId(R.id.pref_unit_fahrenheit_radio_button);


        mRadioGroup.addView(celsiusButton);
        mRadioGroup.addView(fahrenheitButton);

        // Check according to preference
        if (mIsCelsiusUnit) {
            mRadioGroup.check(R.id.pref_unit_celsius_radio_button);
        } else {
            mRadioGroup.check(R.id.pref_unit_fahrenheit_radio_button);
        }


        // Create the framelayout and add the picker
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mRadioGroup);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int checkedId = mRadioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.pref_unit_celsius_radio_button:
                    setIsCelsiusUnit(true);
                    break;
                case R.id.pref_unit_fahrenheit_radio_button:
                    setIsCelsiusUnit(false);
                    break;
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        boolean defaultIsCelsius = getContext()
                .getResources()
                .getBoolean(R.bool.pref_is_celsius_default);
        return a.getBoolean(index, defaultIsCelsius);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        boolean defaultIsCelsius = getContext()
                .getResources()
                .getBoolean(R.bool.pref_is_celsius_default);
        setIsCelsiusUnit(restorePersistedValue ?
                getPersistedBoolean(defaultIsCelsius) : (Boolean) defaultValue);
    }

    private void setIsCelsiusUnit(boolean value) {
        this.mIsCelsiusUnit = value;
        persistBoolean(this.mIsCelsiusUnit);
    }

    private boolean getIsCelsiusUnit() {
        return this.mIsCelsiusUnit;
    }
}
