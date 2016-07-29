package com.restaurantfinder.lazylad91.restaurantfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient mGoogleApiClient;
    private CallRestClient mCallRestClient;
    private GoogleMap mMap;
    private static ArrayList<Marker> markersList;
    private static View view;
    private static Double latitude, longitude;
    private Location mLocation;
    private static LatLngBounds.Builder builder;
    private static Place mPlaceSelected;
    private static Marker mPlaceMarker;
    private Restaurant selectedRestaurant;
    private RestClient mRestClient;
    private Marker mMarker;
    private static ArrayList<Restaurant> restaurantArrayList;
    protected LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ham);
        checkServicesEnable();
        if(!isGooglePlayServicesAvailable(this)){
            Toast.makeText(HomeActivity.this, "Google Play Services Disable Application Wont Work", Toast.LENGTH_SHORT)
                    .show();
        }

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        /* Check Location Services and internet Services*/

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        /*if(mGoogleApiClient!=null){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }}*/
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkServicesEnable();
        if (checkServicesEnable()) {

        //    buildConnectionWithGoogleAPI();
           /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);*/

            //onLocationChanged(null);
        }
    }

    private boolean checkServicesEnable() {
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            network_enabled = cm.getActiveNetwork() != null;

        } catch (Exception ex) {
        }
        if (!gps_enabled) {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.coord), "Please Enable GPS", Snackbar.LENGTH_LONG);
            snackbar.setAction("Enable GPS", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    snackbar.dismiss();
                    startActivityForResult(myIntent, 0);

                }
            }).show();
        }
        if (gps_enabled && !network_enabled) {
            final Snackbar snackbar1 = Snackbar.make(findViewById(R.id.coord), "Please Enable Internet", Snackbar.LENGTH_LONG);
            snackbar1.setAction("Enable Internet", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    snackbar1.dismiss();
                    startActivityForResult(myIntent, 0);
                    //finish();
                }
            }).show();
        }
        if(gps_enabled && network_enabled){

        }
        return gps_enabled && network_enabled;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_List) {
            // Back to list View
            if (restaurantArrayList == null) {
                Intent intent = new Intent(this, SearchMap.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("restaurantList", null);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return true;
            }
            if (restaurantArrayList.size() != 0) {
                Intent intent = new Intent(this, SearchMap.class);
                Bundle bundle = new Bundle();
                if(mLocation!=null) {
                    bundle.putDouble("lat", mLocation.getLatitude());
                    bundle.putDouble("lng", mLocation.getLatitude());
                }
                bundle.putSerializable("restaurantList", restaurantArrayList);
                bundle.putSerializable("selectedRestaurant",selectedRestaurant);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return true;
            }
        }
        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
                // Back to list View
                if (restaurantArrayList == null) {
                    Intent intent = new Intent(this, SearchMap.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("restaurantList", null);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (restaurantArrayList.size() != 0) {
                    Intent intent = new Intent(this, SearchMap.class);
                    Bundle bundle = new Bundle();
                    if(mLocation!=null) {
                        bundle.putDouble("lat", mLocation.getLatitude());
                        bundle.putDouble("lng", mLocation.getLatitude());
                    }
                    bundle.putSerializable("restaurantList", restaurantArrayList);
                    bundle.putSerializable("selectedRestaurant",selectedRestaurant);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return true;
                }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap);
                    if(mLocationRequest!=null) {
                        startLocationUpdates();
                    }
                    else{
                        createLocationRequest();
                    }
                } else {
                    // Permission Denied
                    //Toast.makeText(HomeActivity.this, "Application Wont work", Toast.LENGTH_SHORT)
                            //.show();
                    return;
                    /*if (mMap != null)
                        onMapReady(mMap);*/
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        Log.d("gpsI am getting called", "getting called");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        map.setMyLocationEnabled(true);
        //mMap.setOnMyLocationChangeListener(this.myLocationChangeListener);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        buildConnectionWithGoogleAPI();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

/*
* The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
* set a filter returning only results with a precise address.
*/
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                mPlaceSelected = place;
                selectedRestaurant = new Restaurant();
                selectedRestaurant.setPrice_level(place.getPriceLevel());
                selectedRestaurant.setAddress(place.getAddress().toString());
                selectedRestaurant.setName(place.getName().toString());
                selectedRestaurant.setRating(place.getRating());
                selectedRestaurant.setLat(place.getLatLng().latitude);
                selectedRestaurant.setLng(place.getLatLng().longitude);
                if (mMap != null) {
                    mPlaceMarker = mMap.addMarker(new MarkerOptions()
                            .title(place.getName().toString())
                            .snippet(place.getAddress().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .position(place.getLatLng()));
                    Toast.makeText(getApplicationContext(),
                            "Locating Restaurants Nearby selected Place:", Toast.LENGTH_LONG).show();
                    mCallRestClient = new CallRestClient();
                    mCallRestClient.execute(place.getLatLng());

                }
                Log.d("TAG", "Place: " + place.getName());//get place details here
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("TAG", "An error occurred: " + status);
            }
        });

    }

    /*private void setsearch() {
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                null, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }*/

    /*private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.d(LOG_TAG, "Selected: " + item.description.toString());
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

           *//* mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
            mWebTextView.setText(place.getWebsiteUri() + "");*//*
            *//*if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }*//*
        }
    };*/


    private void buildConnectionWithGoogleAPI() {
        Log.d("gpsI", "buildConnectionWithGoogleAPI i am getting called");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(5000);

        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected())
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected  void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("restaurantList",restaurantArrayList);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        createLocationRequest();
/*        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);*/

        Log.d("gpsI","onConnected i am getting called");
        // mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
                mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
       // Log.d("gpsI",mLocation.toString());
        if (mLocation != null) {
/*            if(mMarker!=null)
                mMarker.remove();*/
            Log.d("gpsI","got the location object");
            LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            addMarker(myLocation, "myLocation", "I am here", BitmapDescriptorFactory.HUE_BLUE);
        }
        /* Code for Reloading activity*/
        Intent intent = this.getIntent();
        if (intent.hasExtra("restaurantList")) {
            bundle = intent.getExtras();
            restaurantArrayList = (ArrayList<Restaurant>) bundle.getSerializable("restaurantList");
            if(intent.hasExtra("selectedRestaurant")){
                selectedRestaurant=(Restaurant)bundle.getSerializable("selectedRestaurant");
            }
            //restaurantArrayList = (ArrayList<Restaurant>)savedInstanceState.getSerializable("restaurantList");
            builder = new LatLngBounds.Builder();
            // mMap.setMyLocationEnabled(true);
            markersList = new ArrayList<Marker>();
            //LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
           if(restaurantArrayList!=null){
            for (Restaurant restaurant : restaurantArrayList) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .title(restaurant.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet("Location:" + restaurant.getAddress() + " Price:" + restaurant.getPrice_level() + " " + "Open Now: " + restaurant.isOpen_now())
                        .position(new LatLng(restaurant.getLat(), restaurant.getLng())));
                builder.include(new LatLng(restaurant.getLat(), restaurant.getLng()));
                markersList.add(marker);

            }

            //restaurantArrayList.add(selectedRestaurant);
            //Collections.sort(restaurantArrayList);

            if(selectedRestaurant!=null) {
                Log.d("lastcheck",selectedRestaurant.toString());
                Marker mPlaceMarker = mMap.addMarker(new MarkerOptions()
                        .title(selectedRestaurant.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet("Location:" + selectedRestaurant.getAddress() + " Price:" + selectedRestaurant.getPrice_level() + " " + "Open Now: " + selectedRestaurant.isOpen_now())
                        .position(new LatLng(selectedRestaurant.getLat(), selectedRestaurant.getLng())));
                markersList.add(mPlaceMarker);
                builder.include(mPlaceMarker.getPosition());

            }//rewrite
            //end

            LatLngBounds bounds;
            bounds = builder.build();
            int padding = 150; // offset from edges of the map in pixels


            //Adding current Location code
            if (mMap != null) {
                if(mLocation!=null) {
                    builder.include(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                    bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
                    mMap.animateCamera(cu);
                    mMap.setTrafficEnabled(false);
                }
            }


        }
        }
    }

    private void addMarker(LatLng myLocation, String myLocation1, String s,Float color) {
        if(mMarker!=null)
            mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions()
                .title(myLocation1)
                .snippet(s).icon(BitmapDescriptorFactory.defaultMarker(color))
                .position(myLocation));
        if(builder!=null) {
            builder.include(new LatLng(myLocation.latitude, myLocation.longitude));
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 80);
            Log.d("calledbund","getting bounds"+builder.toString());
            mMap.animateCamera(cu);
        }
        else {
            Log.d("calledbund","not getting bounds");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(mMarker!=null)
        mMarker.remove();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if(mMarker!=null)
            mMarker.remove();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        addMarker(myLocation, "myLocation", "I am here",BitmapDescriptorFactory.HUE_BLUE);
    }


    private class CallRestClient extends AsyncTask<LatLng, Void, String> {

        final String GOOGLE_KEY = "AIzaSyC9m5N_sL7VREx67zEBq2kyoykdwi3yTu4";

        @Override
        protected String doInBackground(LatLng... latLngs) {
            checkServicesEnable();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latLngs[0].latitude + "," + latLngs[0].longitude + "&radius=500&type=cafe&key=" + GOOGLE_KEY;
            Log.d("url", url);
            return RestClient.makeHttpCall(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*if(mGoogleApiClient.isConnected()){
                stopLocationUpdates();
            }*/
            if (checkServicesEnable()) {
                if(restaurantArrayList!=null)
                restaurantArrayList.clear();
                Log.d("s", s);
                if (markersList != null) {
                    for (Marker marker : markersList) {
                        marker.remove();
                    }
                }
/*                if(mPlaceMarker!=null){
                    mPlaceMarker.remove();
                }*/
                if(mMap!=null)
                    mMap.clear();
                restaurantArrayList = RestClient.parseJsonInBO(s);
                for (Restaurant restaurant : restaurantArrayList) {
                    Log.d("restaurantnu", restaurant.toString());
                }
                //Code of putting markers
                if (restaurantArrayList.size() != 0) {
                    builder = new LatLngBounds.Builder();
                    // mMap.setMyLocationEnabled(true);
                    markersList = new ArrayList<Marker>();
                    //LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                    for (Restaurant restaurant : restaurantArrayList) {
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .title(restaurant.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .snippet("Location:" + restaurant.getAddress() + " Price:" + restaurant.getPrice_level() + " " + "Open Now: " + restaurant.isOpen_now())
                                .position(new LatLng(restaurant.getLat(), restaurant.getLng())));
                        builder.include(new LatLng(restaurant.getLat(), restaurant.getLng()));
                        markersList.add(marker);

                    }
                    //restaurantArrayList.add(selectedRestaurant);
                    //Collections.sort(restaurantArrayList);
                    Marker mPlaceMarker = mMap.addMarker(new MarkerOptions()
                            .title(selectedRestaurant.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .snippet("Location:" + selectedRestaurant.getAddress() + " Price:" + selectedRestaurant.getPrice_level() + " " + "Open Now: " + selectedRestaurant.isOpen_now())
                            .position(new LatLng(selectedRestaurant.getLat(), selectedRestaurant.getLng())));
                    markersList.add(mPlaceMarker);
                    builder.include(mPlaceSelected.getLatLng());
                    //rewrite
                    //end

                    LatLngBounds bounds;
                    bounds = builder.build();
                    int padding = 150; // offset from edges of the map in pixels


                    //Adding current Location code
                    if (mMap != null) {
                        builder.include(mMarker.getPosition());
                        bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);
                        mMap.setTrafficEnabled(false);
                    }
                }


            }
        }
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}