package com.shockn745.moovin5.motivation.add_card_menu;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardInterface;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter to display items in the add card menu
 */
public class AddCardMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int EMPTY_SET = -1;

    public interface AddCardFromCacheCallback {

        /**
         * Method used to add a previously removed card
         * @param viewType view type to retrieve card from cache
         */
        void addCardFromCache(int viewType);
    }

    /**
     * ViewHolder to hold the card descriptions and card viewType
     * Also set the OnClickListener
     */
    private class VH extends RecyclerView.ViewHolder {
        public TextView textView;
        private int mCardViewType;

        public VH(View itemView,
                  final AddCardFromCacheCallback addCardFromCacheCallback,
                  final AddCardMenuCallbacks addCardMenuCallbacks) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.add_card_menu_list_item_text_view);

            // Set OnClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Disable onClickListener on the "EMPTY_SET" message
                    if (mCardViewType != EMPTY_SET) {
                        addCardFromCacheCallback.addCardFromCache(mCardViewType);
                        addCardMenuCallbacks.hideAddCardMenu();
                    }
                }
            });
        }

        public void setCardViewType(int mCardViewType) {
            this.mCardViewType = mCardViewType;
        }
    }

    // Stores the VIEW_TYPES of cards that have been dismissed
    private final ArrayList<Integer> mListDismissedCards;
    private final Activity mActivity;
    private final AddCardFromCacheCallback mAddCardFromCacheCallback;
    private final HashMap<Integer, String> mCardDescriptions;

    public AddCardMenuAdapter(ArrayList<Integer> listDismissedCards,
                              Activity activity,
                              AddCardFromCacheCallback addCardFromCacheCallback) {
        this.mListDismissedCards = listDismissedCards;
        mActivity = activity;
        mAddCardFromCacheCallback = addCardFromCacheCallback;

        // Create the list of card descriptions
        // Binds the card view type with its description
        // Add a description for when no card is in cache
        mCardDescriptions = new HashMap<>();
        mCardDescriptions.put(
                CardInterface.WEATHER_VIEW_TYPE,
                mActivity.getString(R.string.add_card_menu_weather)
        );
        mCardDescriptions.put(
                CardInterface.ROUTE_VIEW_TYPE,
                mActivity.getString(R.string.add_card_menu_route)
        );
        mCardDescriptions.put(
                CardInterface.CALORIES_VIEW_TYPE,
                mActivity.getString(R.string.add_card_menu_calories)
        );
        mCardDescriptions.put(
                EMPTY_SET,
                mActivity.getString(R.string.add_card_menu_empty_set)
        );
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.add_card_menu_list_item, parent, false);
        return new VH(itemView, mAddCardFromCacheCallback, (AddCardMenuCallbacks) mActivity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Display text describing the card< to add
        String textToSet;
        int cardType = mListDismissedCards.get(position);

        textToSet = mCardDescriptions.get(cardType);

        // Display the card description of the cards that are in cache
        // If empty, display a message : mCardDescriptions.get(EMPTY_SET);
        if (textToSet != null) {
            ((VH) holder).textView.setText(textToSet);
            ((VH) holder).setCardViewType(cardType);
        } else {
            throw new IllegalStateException("Card type not supported");
        }
    }

    @Override
    public int getItemCount() {
        return mListDismissedCards.size();
    }

    public String getCardDescription(int cardViewType) {
        return mCardDescriptions.get(cardViewType);
    }

}
