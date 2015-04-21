package com.shockn745.workoutmotivationaltool.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardContact;
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardInterface;
import com.shockn745.workoutmotivationaltool.recyclerview.cards.CardSimple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String LOG_TAG = TestAdapter.class.getSimpleName();

    private ArrayList<CardInterface> mDataSet;
    private ArrayDeque<CardInterface> mLifo;

    public static class SimpleVH extends RecyclerView.ViewHolder {
        public TextView mSimpleTextView;

        public SimpleVH(View itemView) {
            super(itemView);
            this.mSimpleTextView = (TextView) itemView.findViewById(R.id.simple_text_view);
        }
    }

    public static class ContactVH extends RecyclerView.ViewHolder {
        public TextView mPrenomTextView;
        public TextView mNomTextView;
        public TextView mTelTextView;

        public ContactVH(View itemView) {
            super(itemView);
            this.mPrenomTextView = (TextView) itemView.findViewById(R.id.prenom_textView);
            this.mNomTextView = (TextView) itemView.findViewById(R.id.nom_text_view);
            this.mTelTextView = (TextView) itemView.findViewById(R.id.telephone_textView);
        }
    }


    public TestAdapter(ArrayList<CardInterface> dataSet) {
        // Init the dataset
        mDataSet = dataSet;

        // Init LIFO
        mLifo = new ArrayDeque<>();
        mLifo.push(new CardContact("NOM", "PRENOM", "06060606060"));
        mLifo.push(new CardSimple("Texte 1 "));
        mLifo.push(new CardSimple("Texte 2 "));
        mLifo.push(new CardContact("QSDF", "QSDF", "06060606060"));
        mLifo.push(new CardContact("PI", "ORANGE", "06060606060"));
        mLifo.push(new CardSimple("Texte 3 "));
        mLifo.push(new CardContact("NO345M", "FGHGF", "06060606060"));
        mLifo.push(new CardSimple("Texte 4 "));
        mLifo.push(new CardContact("SAA", "VER", "06060606060"));
        mLifo.push(new CardContact("MIIISDf", "sddgsfg", "06060606060"));
        mLifo.push(new CardContact("hhhhh", "hhhhhhh", "06060606060"));
        mLifo.push(new CardContact("aaazaa", "cccc", "06060606060"));

    }


    /**
     * Create the viewHolder
     *
     * @return ViewHolder created
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case CardInterface.CONTACT_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_contact, parent, false);

                return new ContactVH(itemView);

            case CardInterface.SIMPLE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_simple, parent, false);

                return new SimpleVH(itemView);

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

        if (holder instanceof SimpleVH) {
            Log.d(LOG_TAG, "Simple VH");

            SimpleVH simpleHolder = (SimpleVH) holder;

            CardSimple card = (CardSimple) mDataSet.get(position);

            simpleHolder.mSimpleTextView.setText(card.getSimpleText());

        } else if (holder instanceof ContactVH) {
            Log.d(LOG_TAG, "Contact VH");

            ContactVH contactHolder = (ContactVH) holder;

            CardContact card = (CardContact) mDataSet.get(position);

            contactHolder.mNomTextView.setText(card.getNom());
            contactHolder.mPrenomTextView.setText(card.getPrenom());
            contactHolder.mTelTextView.setText(card.getTelephone());
        } else {
            Log.d(LOG_TAG, "ERROR VH not recognized");
        }

        // Enable delete on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Item deleted : " + mDataSet.get(holder.getPosition()).toString());
                removeCard(holder.getPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    // Handle multiple Layouts

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getViewType();
    }

    ///////////////////////////////////
    // Methods to handle the dataset //
    ///////////////////////////////////

    /**
     * Method used to add a card.
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

        // Cache in LIFO
        mLifo.push(toCache);

        // Notify Adapter to refresh (also starts the animation)
        notifyItemRemoved(position);
    }

    public void addCardFromLIFO() {
        try {
            CardInterface toAdd = mLifo.pop();
            addCard(toAdd);
        } catch (NoSuchElementException e) {
            Log.d(LOG_TAG, "Empty LIFO");
        }
    }
}
