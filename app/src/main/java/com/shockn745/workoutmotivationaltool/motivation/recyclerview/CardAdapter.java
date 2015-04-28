package com.shockn745.workoutmotivationaltool.motivation.recyclerview;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardAd;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardBackAtHome;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardCalories;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardInterface;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoading;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardLoadingSimple;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardRoute;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardWeather;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private static final String LOG_TAG = CardAdapter.class.getSimpleName();

    private final ArrayList<CardInterface> mDataSet;
    private final Activity mActivity;

    public CardAdapter(ArrayList<CardInterface> dataSet, Activity activity) {
        // Init the dataset
        mDataSet = dataSet;
        mActivity = activity;
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
            case CardInterface.LOADING_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_loading, parent, false);
                return new CardLoading.LoadingVH(itemView);

            case CardInterface.LOADING_SIMPLE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_loading_simple, parent, false);
                return new CardLoadingSimple.LoadingSimpleVH(itemView);

            case CardInterface.BACK_AT_HOME_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_back_at_home, parent, false);
                return new CardBackAtHome.BackAtHomeVH(itemView);

            case CardInterface.WEATHER_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_weather, parent, false);
                return new CardWeather.WeatherVH(itemView);

            case CardInterface.ROUTE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_route, parent, false);
                return new CardRoute.RouteVH(itemView);

            case CardInterface.CALORIES_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_calories, parent, false);
                return new CardCalories.CaloriesVH(itemView);

            case CardInterface.AD_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_ad, parent, false);
                return new CardAd.AdVH(itemView);
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

        if (holder instanceof CardLoading.LoadingVH) {
            CardLoading.LoadingVH loadingHolder = (CardLoading.LoadingVH) holder;

            CardLoading card = (CardLoading) mDataSet.get(position);

            loadingHolder.mTextView.setText(card.getText());

        } else if (holder instanceof CardLoadingSimple.LoadingSimpleVH) {
            // Includes also CardLoading.LoadingVH
            CardLoadingSimple.LoadingSimpleVH loadingSimpleHolder =
                    (CardLoadingSimple.LoadingSimpleVH) holder;

            CardLoadingSimple card = (CardLoadingSimple) mDataSet.get(position);

            loadingSimpleHolder.mTextView.setText(card.getText());

        } else if (holder instanceof CardBackAtHome.BackAtHomeVH) {
            CardBackAtHome.BackAtHomeVH backAtHomeVH = (CardBackAtHome.BackAtHomeVH) holder;

            CardBackAtHome card = (CardBackAtHome) mDataSet.get(position);

            View cardView = holder.itemView;
            int toolbarHeight = mActivity.findViewById(R.id.motivation_toolbar).getHeight();
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
            marginLayoutParams.topMargin += toolbarHeight;
            cardView.setLayoutParams(marginLayoutParams);

            backAtHomeVH.mTextView.setText(card.getText());

        } else if (holder instanceof CardWeather.WeatherVH) {
            CardWeather.WeatherVH weatherVH = (CardWeather.WeatherVH) holder;

            CardWeather card = (CardWeather) mDataSet.get(position);

            weatherVH.mTextView.setText(card.getText());

        } else if (holder instanceof CardRoute.RouteVH) {
            CardRoute.RouteVH routeVH = (CardRoute.RouteVH) holder;

            CardRoute card = (CardRoute) mDataSet.get(position);

            routeVH.mTextView.setText(card.getText());

        } else if (holder instanceof CardCalories.CaloriesVH) {
            CardCalories.CaloriesVH caloriesVH = (CardCalories.CaloriesVH) holder;

            CardCalories card = (CardCalories) mDataSet.get(position);

            caloriesVH.mTextView.setText(card.getText());

        } else if (holder instanceof CardAd.AdVH) {
            CardAd.AdVH adVH = (CardAd.AdVH) holder;

            CardAd card = (CardAd) mDataSet.get(position);

            adVH.mTextView.setText(card.getText());

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


    /**
     * Remove the loading card(s)
     */
    public void clearLoadingScreen() {
        boolean bothCardsShown = false;
        try {
            mDataSet.remove(0);
        } catch (IndexOutOfBoundsException e) {
            Log.d(LOG_TAG, "First loading card was not yet shown");
        }
        try {
            mDataSet.remove(0);
            bothCardsShown = true;
        } catch (IndexOutOfBoundsException e) {
            Log.d(LOG_TAG, "Second loading card was not yet shown");
            // Only first card removed
            notifyItemRemoved(0);
        }
        if (bothCardsShown) {
            notifyItemRangeRemoved(0, 2);
        }
    }

}
