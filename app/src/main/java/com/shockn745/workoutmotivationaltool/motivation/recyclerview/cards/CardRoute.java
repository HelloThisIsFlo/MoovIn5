package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.shockn745.workoutmotivationaltool.R;

/**
 * Card that display route
 */
public class CardRoute implements CardInterface {

    private static final String LOG_TAG = CardRoute.class.getSimpleName();


    public static class RouteVH extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public MapView mMapView;
        public GoogleMap mMap = null;

        public RouteVH(View itemView) {
            super(itemView);
            this.mMapView = (MapView) itemView.findViewById(R.id.route_map_view);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            Log.d(LOG_TAG, "OnMapReady called");
        }
    }

    private String mPolyline;

    public CardRoute(String polyline) {
        mPolyline = polyline;
    }

    @Override
    public int getViewType() {
        return ROUTE_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

}
