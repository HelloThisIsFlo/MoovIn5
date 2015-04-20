package com.shockn745.workoutmotivationaltool.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

import java.util.ArrayList;

public class testAdapter extends RecyclerView.Adapter<testAdapter.TestHolder> {

    public static final String LOG_TAG = testAdapter.class.getSimpleName();

    private ArrayList<String> mDataSet;

    public static class TestHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public TestHolder(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.test_text_view);
        }
    }

    public testAdapter(ArrayList<String> dataSet) {
        mDataSet = dataSet;
    }


    /**
     * Create the viewHolder
     *
     * @return ViewHolder created
     */
    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_test, parent, false);
        return new TestHolder(cardView);
    }

    /**
     * Replace the content of the view
     *
     * @param holder viewHolder
     * @param position Position of the data
     */
    @Override
    public void onBindViewHolder(final TestHolder holder, int position) {
        holder.mTextView.setText(mDataSet.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Item supprimé : " + mDataSet.get(holder.getPosition()));
                mDataSet.remove(holder.getPosition());
                testAdapter.this.notifyItemRemoved(holder.getPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
