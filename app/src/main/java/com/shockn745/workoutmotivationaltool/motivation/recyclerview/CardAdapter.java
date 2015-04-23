package com.shockn745.workoutmotivationaltool.motivation.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardContact;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardInterface;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardSimple;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private static final String LOG_TAG = TestAdapter.class.getSimpleName();

    private final ArrayList<CardInterface> mDataSet;

    public CardAdapter(ArrayList<CardInterface> dataSet) {
        // Init the dataset
        mDataSet = dataSet;
    }


    /**
     * Create the viewHolder
     *
     * @return ViewHolder created
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        // TODO implement cases for the cards actually used
        switch (viewType) {
            case CardInterface.CONTACT_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_contact, parent, false);

                return new CardContact.ContactVH(itemView);

            case CardInterface.SIMPLE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_simple, parent, false);

                return new CardSimple.SimpleVH(itemView);

            default:
                return null;
        }
    }

    /**
     * Replace the content of the view
     *
     * @param holder viewHolder
     * @param position Position of the data
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        // TODO implement cases for the cards actually used
        if (holder instanceof CardSimple.SimpleVH) {
            Log.d(LOG_TAG, "Simple VH");

            CardSimple.SimpleVH simpleHolder = (CardSimple.SimpleVH) holder;

            CardSimple card = (CardSimple) mDataSet.get(position);

            simpleHolder.mSimpleTextView.setText(card.getSimpleText());

        } else if (holder instanceof CardContact.ContactVH) {
            Log.d(LOG_TAG, "Contact VH");

            CardContact.ContactVH contactHolder = (CardContact.ContactVH) holder;

            CardContact card = (CardContact) mDataSet.get(position);

            contactHolder.mNomTextView.setText(card.getNom());
            contactHolder.mPrenomTextView.setText(card.getPrenom());
            contactHolder.mTelTextView.setText(card.getTelephone());
        } else {
            Log.d(LOG_TAG, "ERROR VH not recognized");
        }

    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    /**
     * Handles multiple layouts <br>
     * Returns the viewType used in : {@link #onCreateViewHolder(android.view.ViewGroup, int)}
     * @param position Position of the card
     * @return Type of the card layout
     */
    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getViewType();
    }



    //////////////////////////////////////////
    // Methods to implement DismissCallback //
    //////////////////////////////////////////

    /**
     * Determine whether a card wan be dismissed or not
     * @param position Position of the card
     * @return true if dismissable
     */
    @Override
    public boolean canDismiss(int position) {
        return mDataSet.get(position).canDismiss();
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int position) {
        removeCard(position);
    }

    ///////////////////////////////////
    // Methods to handle the dataset //
    ///////////////////////////////////

    /**
     * Method used to add a card at the end.
     * Handle the insertion in the dataset and in the adapter.
     * Triggers the animation.
     * @param toAdd Card to add at the end
     */
    public void addCard(CardInterface toAdd) {
        // Element inserted at the end
        // So size of dataset before insertion == position of inserted element
        int position = mDataSet.size();

        mDataSet.add(toAdd);
        notifyItemInserted(position);
    }

    /**
     * Method used to remove a card.
     * Handle the deletion from the dataset and the adapter
     * Triggers the animation.
     * @param position Position of the card to remove
     */
    public void removeCard(int position) {
        // Remove from dataset
        CardInterface toCache = mDataSet.remove(position);

        // Notify Adapter to refresh (also starts the animation)
        notifyItemRemoved(position);
    }


    public void clearLoadingScreen() {
        //
        mDataSet.remove(0);
        try {
            mDataSet.remove(0);
        } catch (IndexOutOfBoundsException e) {
            Log.d(LOG_TAG, "Second card was not shown");
        }
        notifyItemRangeRemoved(0, 2);
    }

}
