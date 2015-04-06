package com.shockn745.workoutmotivationaltool;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/**
 * Created by Shock on 06.04.15.
 */
public class NumberPickerPreference  extends DialogPreference{

    // TODO move from hard coded to resource file
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 60;

    private NumberPicker mPicker;
    private int mValue;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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

        // Create the framelayout and add the picker
        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mPicker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mPicker.setMinValue(MIN_VALUE);
        mPicker.setMaxValue(MAX_VALUE);
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
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.mValue = value;
        persistInt(this.mValue);
    }

    public int getValue() {
        return this.mValue;
    }
}