package com.shockn745.moovin5.motivation.recyclerview;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.MotivationActivity;
import com.shockn745.moovin5.motivation.add_card_menu.AddCardMenuAdapter;
import com.shockn745.moovin5.motivation.recyclerview.animation.SwipeDismissRecyclerViewTouchListener;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardAd;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardBackAtHome;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardInterface;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardLoading;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardLoadingSimple;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardRoute;
import com.shockn745.moovin5.motivation.recyclerview.cards.CardWeather;
import com.shockn745.moovin5.motivation.recyclerview.cards.calories.CaloriesAdapter;
import com.shockn745.moovin5.motivation.recyclerview.cards.calories.CardCalories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Adapter for the list of cards
 *
 * @author Florian Kempenich
 */
public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SwipeDismissRecyclerViewTouchListener.DismissCallbacks,
        AddCardMenuAdapter.AddCardFromCacheCallback {

    private static final String LOG_TAG = CardAdapter.class.getSimpleName();

    private final ArrayList<CardInterface> mDataSet;
    private final MotivationActivity mActivity;

    // Card cache variables
    private final HashMap<Integer, CardInterface> mRemovedCards;
    private final ArrayList<Integer> mRemovedCardsViewTypes;
    private final AddCardMenuAdapter mAddCardMenuAdapter;

    private DrawPolylineCallback mDrawPolylineCallback;

    public interface DrawPolylineCallback {
        void drawPolylineCallback();
    }

    public CardAdapter(ArrayList<CardInterface> dataSet,
                       Activity activity,
                       DrawPolylineCallback drawPolylineCallback) {
        // Init the dataset
        mDataSet = dataSet;
        mActivity = (MotivationActivity) activity;
        mDrawPolylineCallback = drawPolylineCallback;
        mRemovedCards = new HashMap<>();
        mRemovedCardsViewTypes = new ArrayList<>();
        // Notify AddCardMenuAdapter that no cards are in cache
        mRemovedCardsViewTypes.add(AddCardMenuAdapter.EMPTY_SET);

        // Set AddCardMenuAdapter
        mAddCardMenuAdapter = new AddCardMenuAdapter(mRemovedCardsViewTypes, mActivity, this);

        RecyclerView recyclerView =
                (RecyclerView) activity.findViewById(R.id.add_card_menu_recycler_view);

        recyclerView.setAdapter(mAddCardMenuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }


    /**
     * Create the viewHolder
     *
     * @return ViewHolder created
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView;
        switch (viewType) {
            case CardInterface.LOADING_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_loading, parent, false);
                return createLoadingVH(itemView);

            case CardInterface.LOADING_SIMPLE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_loading_simple, parent, false);
                return new CardLoadingSimple.LoadingSimpleVH(itemView);

            case CardInterface.BACK_AT_HOME_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_back_at_home, parent, false);
                return createBackAtHomeVH(itemView);

            case CardInterface.WEATHER_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_weather, parent, false);
                // Create the weather VH, pass the ratio parameter
                return new CardWeather.WeatherVH(
                        itemView,
                        mActivity.getResources().getFraction(R.fraction.card_weather_ratio, 1, 1)
                );

            case CardInterface.ROUTE_VIEW_TYPE:
                itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_route, parent, false);
                return createRouteVH(itemView);

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
            bindBackAtHomeCard((CardBackAtHome.BackAtHomeVH) holder, position);

        } else if (holder instanceof CardWeather.WeatherVH) {
            bindWeatherCard((CardWeather.WeatherVH) holder, position);

        } else if (holder instanceof CardRoute.RouteVH) {
            CardRoute.RouteVH routeVH = (CardRoute.RouteVH) holder;

            CardRoute card = (CardRoute) mDataSet.get(position);

            // Add the MapView to the FrameLayout
            // if necessary remove from the previous FrameLayout
            if (mActivity.getMapView().getParent() != null) {
                ((FrameLayout) mActivity.getMapView().getParent()).removeAllViews();
            }
            routeVH.mFrameLayout.addView(mActivity.getMapView());

        } else if (holder instanceof CardCalories.CaloriesVH) {
            bindCaloriesCard((CardCalories.CaloriesVH) holder, position);

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
     * Method used to add a previously removed card
     * @param viewType view type to retrieve card from cache
     */
    @Override
    public void addCardFromCache(int viewType) {
        // Retrieve card and delete it from cache
        CardInterface cardToAdd = mRemovedCards.get(viewType);
        mRemovedCards.remove(viewType);

        // Update mRemovedCardsViewTypes
        mRemovedCardsViewTypes.clear();
        Set<Integer> cardViewTypesSet = mRemovedCards.keySet();
        if (!cardViewTypesSet.isEmpty()) {
            mRemovedCardsViewTypes.addAll(mRemovedCards.keySet());
        } else {
            mRemovedCardsViewTypes.add(AddCardMenuAdapter.EMPTY_SET);
        }
        mAddCardMenuAdapter.notifyDataSetChanged();

        // Add card
        addCard(cardToAdd);
        String cardDescription = mAddCardMenuAdapter.getCardDescription(viewType);
        if (cardDescription != null) {
            cardDescription = mActivity.getString(R.string.add_card_menu_toast_part_1)
                    + " "
                    + cardDescription
                    + " "
                    + mActivity.getString(R.string.add_card_menu_toast_part_2);
            Toast.makeText(mActivity, cardDescription, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method used to remove a card.
     * Handle the deletion from the dataset and the adapter
     * Triggers the animation.
     * Also cache the removed cards in a stack
     * @param position Position of the card to remove
     */
    public void removeCard(int position) {
        // Remove from dataset
        CardInterface toCache = mDataSet.remove(position);

        // Cache the card and the viewtype
        mRemovedCards.put(toCache.getViewType(), toCache);
        mRemovedCardsViewTypes.clear();
        mRemovedCardsViewTypes.addAll(mRemovedCards.keySet());
        mAddCardMenuAdapter.notifyDataSetChanged();

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



    ///////////////////////////
    // Bind/create method(s) //
    ///////////////////////////

    /**
     * Binds the calorie card : set the recyclerView (adapter + dynamic height)
     * @param holder Calorie card holder
     * @param position position of calorieCard in dataset
     */
    private void bindCaloriesCard(CardCalories.CaloriesVH holder, int position) {

        CardCalories card = (CardCalories) mDataSet.get(position);

        holder.headerTextView.setText(card.getHeaderText());
        holder.caloriesTextView.setText(card.getCaloriesText());

        // Set the recycler view
        CardCalories.CaloriesItem[] caloriesDataSet = card.getItems();
        CaloriesAdapter adapter = new CaloriesAdapter(caloriesDataSet);
        holder.recyclerView.setAdapter(adapter);

        holder.recyclerView.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext())
        );

        // Notify the recyclerView that its size won't change (better perfs)
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setItemAnimator(null); //Deactivate animations

        // Set the height of the recyclerview depending on the number of elements
        ViewGroup.LayoutParams layoutParams = holder.recyclerView.getLayoutParams();
        float test = mActivity.getResources().getDimension(R.dimen.calories_recycler_view_list_item_height);
        layoutParams.height = (int) (card.getItems().length
                * mActivity.getResources().getDimension(R.dimen.calories_recycler_view_list_item_height));

        holder.recyclerView.setLayoutParams(layoutParams);

    }

    /**
     * Binds the weather card, set the temp & forecast texts and set the corresponding image
     * @param weatherVH Weather card holder
     * @param position position of the WeatherCard in the dataset
     */
    private void bindWeatherCard(CardWeather.WeatherVH weatherVH, int position) {
        CardWeather card = (CardWeather) mDataSet.get(position);

        weatherVH.mTempTextView.setText(card.getmTempText());
        weatherVH.mForecastTextView.setText(card.getmForecastText());
        weatherVH.mImageView.setImageResource(card.getmImageResId());

    }

    private void bindBackAtHomeCard(CardBackAtHome.BackAtHomeVH backAtHomeVH, int position) {

        CardBackAtHome card = (CardBackAtHome) mDataSet.get(position);

        backAtHomeVH.mBackAtHomeTimeTextView.setText(card.getBackAtHomeTimeString());
        backAtHomeVH.mMotivationalTextView.setText(card.getMotivationalString());

    }

    /**
     * Creates the BackAtHome holder and sets the margin for the cardView.
     * @param itemView Base layout (here cardView)
     * @return the holder created
     */
    private CardBackAtHome.BackAtHomeVH createBackAtHomeVH(View itemView) {
        CardBackAtHome.BackAtHomeVH holder = new CardBackAtHome.BackAtHomeVH(itemView);

        // Set the margin for the cardview
        View cardView = holder.itemView;

        int toolbarHeight = mActivity.findViewById(R.id.motivation_toolbar).getHeight();

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        marginLayoutParams.topMargin += toolbarHeight;
        cardView.setLayoutParams(marginLayoutParams);

        return holder;
    }

    /**
     * Creates the LoadingCard holder and sets the margin for the cardView.
     * @param itemView Base layout (here cardView)
     * @return the holder created
     */
    private CardLoading.LoadingVH createLoadingVH(View itemView) {
        CardLoading.LoadingVH holder = new CardLoading.LoadingVH(itemView);

        // Set the margin for the cardview
        View cardView = holder.itemView;

        int toolbarHeight = mActivity.findViewById(R.id.motivation_toolbar).getHeight();

        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        marginLayoutParams.topMargin += toolbarHeight;
        cardView.setLayoutParams(marginLayoutParams);

        return holder;
    }

    /**
     * Create the RouteCard holder and draw the polyline on the map
     * @param itemView Base layout (here cardView)
     * @return the holder created
     */
    private CardRoute.RouteVH createRouteVH(final View itemView) {
        CardRoute.RouteVH holder = new CardRoute.RouteVH(itemView);

        final ViewTreeObserver vto = itemView.getViewTreeObserver();

        // Draw polyline after the map is displayed
        // Required because the lite mode does not support the extended version of :
        // CameraUpdateFactory.newLatLngBounds()
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Draw the polyline route
                // But only once
                itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                try {
                    mDrawPolylineCallback.drawPolylineCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return holder;
    }


}
