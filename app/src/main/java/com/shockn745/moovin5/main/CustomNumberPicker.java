package com.shockn745.moovin5.main;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.shockn745.moovin5.R;

/**
 * Custom number picker used to increase the sizeof the text in NumberPicker, and change its color
 *
 * @author Kempenich Florian
 */
public class CustomNumberPicker extends NumberPicker {

    public CustomNumberPicker(Context context) {
        super(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void addView(@NonNull View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(@NonNull View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(@NonNull View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if(view instanceof EditText){
            ((EditText) view).setTextSize(35);
            ((EditText) view).setTextColor(
                    view.getContext().getResources().getColor(R.color.textColor)
            );
        }
    }
}
