package com.example.hp.navigation_drawer;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    final static int REQUEST_LOCATION = 199;
    private GoogleApiClient googleApiClient;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
//    private String[] mLikelyPlaceNames;
//    private String[] mLikelyPlaceAddresses;
//    private String[] mLikelyPlaceAttributions;
//    private LatLng[] mLikelyPlaceLatLngs;
//    private float[] mLikelyPlaceRatings;
//    private int[] mLikelyPlacePrices;

    //Lists, used for selecting current place
    ArrayList<String> mLikelyPlaceNames;
    ArrayList<String> mLikelyPlaceAddresses;
    ArrayList<String> mLikelyPlaceAttributions;
    ArrayList<LatLng> mLikelyPlaceLatLngs;
    ArrayList<Float> mLikelyPlaceRatings;
    ArrayList<Integer> mLikelyPlacePrices;


    // Custom Dialog elements Initialization
    private View inputPlaceType;
    private Button FindPlace;

    // showCustomPlace() variables
    String arr[];
    private List<Integer> list;
    private CheckBox Bakery;
    private CheckBox Cafe;
    private CheckBox Food;
    private CheckBox Restaurant;
    private CheckBox Meal_Delivery;
    private CheckBox Meal_takeaway;

    AlertDialog alert1;
    Boolean alert_flag = false;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    SupportMapFragment mapFragment;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Login Implementation


            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener(){
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){

                    if(firebaseAuth.getCurrentUser()== null){
                        Intent loginIntent =new Intent(MainActivity.this,LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
                }
            };

//        End of Login



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        Button logout_btn = findViewById(R.id.logout_btn);


            // logout
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        //setContentView(R.layout.content_main);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //turn GPS On
        if(!hasGPSDevice(MainActivity.this)){
            Toast.makeText(MainActivity.this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Toast.makeText(MainActivity.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.option_get_place) {
            //showCurrentPlace();
            showCustomPlace();
        }
        if(id == R.id.show_all){
            showCurrentPlace();
        }
        if (id == R.id.logout_btn) {
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle args = new Bundle();

//      Boolean fragment_flag= false;

        FragmentManager manager = getFragmentManager();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();


        if(mapFragment.isAdded())
            sFm.beginTransaction().hide(mapFragment).commit();

        if (id == R.id.nav_places) {
            if(!mapFragment.isAdded()) {
                sFm.beginTransaction().replace(R.id.map, mapFragment).commit();
            }else {
                sFm.beginTransaction().show(mapFragment).commit();
            }
            EmptyFragment placeDetails =new EmptyFragment();
            manager.beginTransaction()
                    .replace(R.id.content_frame,placeDetails)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_place_details) {
            args.putStringArrayList("PlaceNames", mLikelyPlaceNames);
            PlaceDetails placeDetails =new PlaceDetails();
            placeDetails.setArguments(args);
            manager.beginTransaction()
                    .replace(R.id.content_frame,placeDetails)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_place_reviews) {
            manager.beginTransaction()
                    .replace(R.id.content_frame,new PlaceReview())
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


         /*
    check for GPS device
     */

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        return providers != null && providers.contains(LocationManager.GPS_PROVIDER);
    }

    /*
    Function for enabling location
     */
    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission")
            final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new ArrayList<String>();
                                mLikelyPlaceAddresses = new ArrayList<String>();
                                mLikelyPlaceAttributions = new ArrayList<String>();
                                mLikelyPlaceLatLngs = new ArrayList<LatLng>();
                                mLikelyPlaceRatings = new ArrayList<Float>();
                                mLikelyPlacePrices = new ArrayList<Integer>();

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                    mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                    mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                    mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                    mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                    mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Prompts the user to search type of a place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */

    private void showCustomPlace() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog,null);
        builder.setView(view).setTitle("Choose Preferences:  ").setCancelable(true);
        alert1 = builder.create();
        alert1.show();
        alert_flag = true;



        Bakery = view.findViewById(R.id.cb1);
        Cafe = view.findViewById(R.id.cb2);
        Food = view.findViewById(R.id.cb3);
        Restaurant = view.findViewById(R.id.cb4);
        Meal_Delivery = view.findViewById(R.id.cb5);
        Meal_takeaway = view.findViewById(R.id.cb6);

    }

    /*
       This function will be triggered when "Get Place" button in the dialogue of showCustomPlace
       will be clicked to show relevant places
    */

    public void btn_click(View view){

        if (mMap == null) {
            return;
        }
        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission")
            final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.

                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }
                                mLikelyPlaceNames = new ArrayList<String>();
                                mLikelyPlaceAddresses = new ArrayList<String>();
                                mLikelyPlaceAttributions = new ArrayList<String>();
                                mLikelyPlaceLatLngs = new ArrayList<LatLng>();
                                mLikelyPlaceRatings = new ArrayList<Float>();
                                mLikelyPlacePrices = new ArrayList<Integer>();

                                arr = new String[count];


                                int i = 0;

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    list = placeLikelihood.getPlace().getPlaceTypes();

                                    for (int j = 0; j < list.size(); j++) {
                                        int a = list.get(j);

                                        if (a == Place.TYPE_BAKERY && Bakery.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        } else if (a == Place.TYPE_CAFE && Cafe.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        } else if (a == Place.TYPE_FOOD && Food.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        } else if (a == Place.TYPE_RESTAURANT && Restaurant.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        } else if (a == Place.TYPE_MEAL_DELIVERY && Meal_Delivery.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        } else if (a == Place.TYPE_MEAL_TAKEAWAY && Meal_takeaway.isChecked()) {
                                            mLikelyPlaceNames.add((String) placeLikelihood.getPlace().getName());
                                            mLikelyPlaceAddresses.add((String) placeLikelihood.getPlace().getAddress());
                                            mLikelyPlaceAttributions.add((String) placeLikelihood.getPlace().getAttributions());
                                            mLikelyPlaceLatLngs.add(placeLikelihood.getPlace().getLatLng());
                                            mLikelyPlaceRatings.add(placeLikelihood.getPlace().getRating());
                                            mLikelyPlacePrices.add(placeLikelihood.getPlace().getPriceLevel());
                                            break;
                                        }
                                    }

                                    i++;

                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }


    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */


    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs.get(which);
                String markerSnippet = mLikelyPlaceAddresses.get(which);
                Float markerRating = mLikelyPlaceRatings.get(which);
                Integer markerPriceLevels = mLikelyPlacePrices.get(which);
                if (mLikelyPlaceAttributions.get(which) != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions.get(which);
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames.get(which))
                        .position(markerLatLng)
                        .snippet(markerSnippet + "\n" + "Rating: " + markerRating
                                + "\n" + "Price level: "+ markerPriceLevels));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        int i = 0;
        int place_list_size = mLikelyPlaceNames.size();
        String[] PlaceNames;
        if(place_list_size != 0){
        PlaceNames = new String[place_list_size];
        for(String mLikelyPlaceName: mLikelyPlaceNames){

            PlaceNames[i] = mLikelyPlaceName;
            i++;
            if (i > (place_list_size - 1)) {
                break;
            }
        }
        }else{
            PlaceNames = new String[place_list_size+1];
            PlaceNames[0] = "No Places Found";
        }


        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(PlaceNames, listener)
                .show();

        if(alert_flag)
        alert1.dismiss();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
