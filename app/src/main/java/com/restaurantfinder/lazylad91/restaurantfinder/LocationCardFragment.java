package com.restaurantfinder.lazylad91.restaurantfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LocationCardFragment extends Fragment {

    private LocationAdapter mAdapter;
    private RecyclerView recyclerView;
    private static List<Restaurant> mRestaurantList;

    public static LocationCardFragment newInstance(List<Restaurant> restaurantList) {
        LocationCardFragment fragment = new LocationCardFragment();
        mRestaurantList = restaurantList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_location_card_fragment,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.location_recycler_view_card);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        updateUI();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if(mRestaurantList!=null && mRestaurantList.size()!=0) {
            List<Restaurant> restaurants = mRestaurantList;

            if (mAdapter == null) {
                mAdapter = new LocationAdapter(restaurants);
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class LocationHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mNameTextView;
        private ImageView mImageView;
        private TextView mAdressTextView;
        private TextView mMoney;
        private TextView mOpenNow;
        private TextView mRating1;

       // private Space mSpace;
       // private Switch mSwitch;
       /* private TextView mPriceTextView;
        private RatingBar mRatingBar;*/

        private Restaurant mRestaurant;

        public LocationHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mNameTextView = (TextView) itemView.findViewById(R.id.nameCardTextView);
            mImageView = (ImageView) itemView.findViewById(R.id.restaurantImageView);
            mAdressTextView = (TextView) itemView.findViewById(R.id.adressTextView1);
            mMoney = (TextView) itemView.findViewById(R.id.money);
            mOpenNow = (TextView)itemView.findViewById(R.id.openNow);
            mRating1 = (TextView)itemView.findViewById(R.id.rating1);
           // mSpace = (Space) itemView.findViewById(R.id.space1);
            //mSwitch = (Switch) itemView.findViewById(R.id.switch1);
           // mPriceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            //mRatingBar  = (RatingBar) itemView.findViewById(R.id.ratingBar);
            // mRatingBar.setBackgroundColor(Color.YELLOW);
        }

        public void bindRestaurant(Restaurant restaurant) {
            mRestaurant = restaurant;
            Log.d("nameof",restaurant.getName());
            mNameTextView.setText(restaurant.getName());
           // mAdressTextView.setText(restaurant.getAddress());
            mAdressTextView.setText(restaurant.getAddress());
            Log.d("photolink","https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurant.getPhoto_reference()+"&key=AIzaSyC9m5N_sL7VREx67zEBq2kyoykdwi3yTu4");
            Picasso.with(getActivity())
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurant.getPhoto_reference()+"&key=AIzaSyC9m5N_sL7VREx67zEBq2kyoykdwi3yTu4")
                    .fit()
                    .into(mImageView);
            if(restaurant.isOpen_now()){
                mOpenNow.setText("Open Now");
            }
            else{
                mOpenNow.setText("Closed");
            }
            if(restaurant.getRating()!=null) {
                mRating1.setText(restaurant.getRating().toString()+"/5");
            }else {
                mRating1.setText(0+"/5");
            }
            mMoney.setText("");
            /*Code for putting price level to show $ sign*/
            if(restaurant.getPrice_level()!=0) {
                for(int i=0; i<restaurant.getPrice_level();i++){
                    mMoney.append("$");
                }
            }else {
                mMoney.setText("");
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
          /*  Intent intent = new Intent(getActivity(), ExpenseDetail.class);
            intent.putExtra("Expense_Id",mExpense.getId());
            startActivity(intent);*/
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+mRestaurant.getLat()+","+mRestaurant.getLng()+"&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapIntent);

            }

        }


    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Restaurant> mRestaurants;

        public LocationAdapter(List<Restaurant> restaurants) {
            mRestaurants = restaurants;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.activity_card_list, parent, false);
            return new LocationHolder(view);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Restaurant restaurant = mRestaurants.get(position);
            holder.bindRestaurant(restaurant);
        }

        @Override
        public int getItemCount() {
            return mRestaurants.size();
        }

    }

}
