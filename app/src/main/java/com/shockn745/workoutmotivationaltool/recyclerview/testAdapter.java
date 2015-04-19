package com.shockn745.workoutmotivationaltool.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

public class testAdapter extends RecyclerView.Adapter<testAdapter.TestHolder> {

    private String[] mDataSet;

    public static class TestHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public TestHolder(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.test_text_view);
        }
    }

    public testAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }

    /**
     * Create the viewHolder
     *
     * @return ViewHolder created
     */
    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_test, null);
        return new TestHolder(v);
    }

    /**
     * Replace the content of the view
     *
     * @param holder viewHolder
     * @param position Position of the data
     */
    @Override
    public void onBindViewHolder(TestHolder holder, int position) {
        holder.mTextView.setText(mDataSet[position]);
    }


    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
