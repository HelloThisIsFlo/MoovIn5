package com.shockn745.moovin5.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.shockn745.moovin5.R;

/**
 * Class that defines a custom DialogPreferences used to display a numberpicker in preferences
 *
 * @author Florian Kempenich
 */
class NumberPickerPreference extends DialogPreference {

    private int mMinValue;
    private int mMaxValue;

    private NumberPicker mPicker;
    private int mValue;

    @SuppressWarnings("unused")
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mMinValue = getContext().getResources().getInteger(R.integer.pref_duration_min);
        mMaxValue = getContext().getResources().getInteger(R.integer.pref_duration_max);
    }

    @Override
    protected View onCreateDialogView() {

        // Create the layout parameters for the numberPicker
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER;

        // Create the picker and set the layout parameters
        mPicker = new NumberPicker(getContext());
        mPicker.setLayoutParams(layoutParams);
        // Disable focus for the elements of the picker (disable keyboard)
        mPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Create the framelayout and add the picker
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mPicker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        mPicker.setMinValue(mMinValue);
        mPicker.setMaxValue(mMaxValue);
        mPicker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setValue(mPicker.getValue());
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mMinValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mMinValue) : (Integer) defaultValue);
    }

    private void setValue(int value) {
        this.mValue = value;
        persistInt(this.mValue);
    }

    private int getValue() {
        return this.mValue;
    }
}