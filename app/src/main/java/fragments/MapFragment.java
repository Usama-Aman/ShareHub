package fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobiletouch.sharehub.ApplicationStartActivity;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import models.MapLocationResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.DialogFactory;
import utility.GPSTracker;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.mobiletouch.sharehub.MainActivity.tabHome;

/**
 * Mubashir 5/15/2018
 */
public class MapFragment extends Fragment implements LocationListener, View.OnClickListener {

    private static final View TODO = null;
    MapView mMapView;
    private GoogleMap googleMap;
    EditText search_et;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    public LatLng latLng;
    public static ArrayList<MapLocationResponse.MapData> EventListDataMap = new ArrayList<MapLocationResponse.MapData>();
    FrameLayout progressBar;
    private AppCompatActivity mContext;
    ImageView ivHomeMap;
    RelativeLayout rlMainMap;
    private LatLngBounds.Builder bounds;
    private Button btnBarCode;
    GPSTracker gpsTracker;
    int i;
    View rootView;
    private List<Places> places = new ArrayList<>();
    Handler handler;
    int zoom = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }


        AppUtils.checkGPS(getActivity());

        i = 0;
        mContext = (AppCompatActivity) getActivity();
        CastViews(rootView);
        gpsTracker = new GPSTracker(mContext);
        //Disable Home Screen map button
        tabHome.setEnabled(false);
        MapsInitializer.initialize(getActivity());
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        handler = new Handler();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);


                // For dropping a marker at a point on the Map

                if (mMapView != null &&
                        mMapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
                    View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);

                }

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (ApplicationStartActivity.openId == 0) {
                            //Enable Home Screen map button
                            tabHome.setEnabled(true);
                        }

                        int position = (int) (marker.getTag());
                        AppUtils.hideKeyboard(mContext);
                        EventDetailsFragment Frag = new EventDetailsFragment();
                        Bundle args = new Bundle();
                        if (EventListDataMap != null && EventListDataMap.size() > 0) {
                            for (int i = 0; i < EventListDataMap.size(); i++)
                                if (EventListDataMap.get(i).getEventId() == position) {
                                    args.putString("event_id", EventListDataMap.get(i).getEventId().toString());
                                    //   URLogs.m(EventListDataMap.get(i).getEventId().toString() + ":Event Id:" + position);
                                    break;
                                }
                        }
                        args.putString("isSelfProfile", "mapList");

                        Frag.setArguments(args);
                        if (ApplicationStartActivity.openId == 1) {
                            (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.mapcontainer, Frag, "Fragment").addToBackStack(null).commit();
                        } else {
                            (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.container, Frag, "Fragment").addToBackStack(null).commit();
                        }
                    }
                });

                // For zooming automatically to the location of the marker
//                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        if (ApplicationStartActivity.openId == 0) {
//                            //Enable Home Screen map button
//                            tabHome.setEnabled(true);
//                        }
//
//                        int position = (int) (marker.getTag());
//                        AppUtils.hideKeyboard(mContext);
//                        EventDetailsFragment Frag = new EventDetailsFragment();
//                        Bundle args = new Bundle();
//                        if (EventListDataMap != null && EventListDataMap.size() > 0) {
//                            for (int i = 0; i < EventListDataMap.size(); i++)
//                                if (EventListDataMap.get(i).getEventId() == position) {
//                                    args.putString("event_id", EventListDataMap.get(i).getEventId().toString());
//                                    //   URLogs.m(EventListDataMap.get(i).getEventId().toString() + ":Event Id:" + position);
//                                    break;
//                                }
//                        }
//                        args.putString("isSelfProfile", "mapList");
//
//                        Frag.setArguments(args);
//                        if (ApplicationStartActivity.openId == 1) {
//                            (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.mapcontainer, Frag, "Fragment").addToBackStack(null).commit();
//                        } else {
//                            (getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.container, Frag, "Fragment").addToBackStack(null).commit();
//                        }
//                        return false;
//                    }
//                });


                if (EventListDataMap != null) {
                    if (EventListDataMap.size() > 0) {
                        // setUpMap();
                        if (googleMap != null)
                            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                @Override
                                public void onCameraMove() {
                                    if (handler != null & r != null)
                                        handler.removeCallbacks(r);
                                    handler.postDelayed(r, 600);
                                }
                            });
                    } else {
                        if (!AppUtils.isOnline(getActivity())) {
                            DialogFactory.showDropDownNotification(getActivity(),
                                    mContext.getString(R.string.alert_information),
                                    mContext.getString(R.string.alert_internet_connection));
                        }
                        try {

                            showProgressBar(true);
                            GetEventsPoints();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if (!AppUtils.isOnline(getActivity())) {
                        DialogFactory.showDropDownNotification(getActivity(),
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internet_connection));
                    }
                    try {

                        showProgressBar(true);
                        GetEventsPoints();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        });
        // implement TextWatcher on Edit text
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 3) {
                    googleMap.clear();

                    if (EventListDataMap != null) {
                        if (EventListDataMap.size() > 0) {
                            for (int i = 0; i < EventListDataMap.size(); i++) {
                                if (containsIgnoreCase(EventListDataMap.get(i).getEventTitle(), s.toString())) {

                                    drawMarker(new LatLng(Double.parseDouble(EventListDataMap.get(i).getEventVenueLat()), Double.parseDouble(EventListDataMap.get(i).getEventVenueLong())), EventListDataMap.get(i).getEventTitle(), EventListDataMap.get(i).getThumbImage(), i, EventListDataMap.get(i).getEventId());

                                    LatLng coordinate = new LatLng(Double.parseDouble(EventListDataMap.get(i).getEventVenueLat()), Double.parseDouble(EventListDataMap.get(i).getEventVenueLong())); //Store these lat lng values somewhere. These should be constant.
                                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                            coordinate, 15);
                                    googleMap.animateCamera(location);
                                }
                            }
                        }
                    }
                } else {
                    if (EventListDataMap != null) {
                        if (EventListDataMap.size() > 0) {
                            setUpMap();
                        }
                    }
                }
            }
        });

        ivHomeMap.setOnClickListener(this);
        btnBarCode.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }

        if (locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        return rootView;
    }

    private void getDeviceLocation() {
        try {

        } catch (SecurityException e) {
            //Log.e("Exception: %s", e.getMessage());
        }
    }

    // Casting views
    public void CastViews(View view) {
        mMapView = view.findViewById(R.id.mapView);
        search_et = view.findViewById(R.id.search_et);
        progressBar = view.findViewById(R.id.progressBar);
        ivHomeMap = view.findViewById(R.id.ivHomeMap);
        rlMainMap = view.findViewById(R.id.rlMainMap);
        btnBarCode = view.findViewById(R.id.bar_code_bt);
        if (ApplicationStartActivity.openId == 1) {
            ivHomeMap.setVisibility(View.VISIBLE);
            ivHomeMap.bringToFront();
        } else {
            ivHomeMap.setVisibility(View.GONE);

        }
        rlMainMap.bringToFront();

    }

    Runnable r = new Runnable() {
        public void run() {
            googleMap.clear();
            setUpMap();
            handler.removeCallbacks(this);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {

        if (googleMap != null) {
            googleMap.clear();
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
        if (ApplicationStartActivity.openId == 0) {
            //Enable Home Screen map button
            tabHome.setEnabled(true);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    // create custom location Marker
    private Bitmap getMarkerBitmapFromView(String event, String image) {
        ////Log.e("Marker Image:", event + "---" + image);
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_pin, null);
        TextView markerTextView = customMarkerView.findViewById(R.id.txt_pin);
        ImageView EventView = customMarkerView.findViewById(R.id.img_event);

        markerTextView.setText(event);
        EventView.bringToFront();

        if (AppUtils.isSet(image)) {
            Picasso.with(getActivity())
                    .load(image)   //
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(EventView);
        }
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);


        return returnedBitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap == null) {
            // URLogs.m("Map is Null");
            return;
        } else {
            // URLogs.m("Map is not Null");
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            googleMap.animateCamera(cameraUpdate);
            if (locationManager != null)
                locationManager.removeUpdates(this);
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void drawMarker(LatLng point, String Title, String ImageUrl, int pos, int eventId) {
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(point);
        markerOptions.icon(bitmapDescriptorFromVector(mContext));
        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(Title);
        markerOptions.draggable(false);
        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions).setTag(eventId);


        // Creating an instance of MarkerOptions
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            // URLogs.m(">LOLLIPOP");
//            if (getActivity() != null) {
//                if (googleMap != null)
//                    googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(Title, ImageUrl)))).setTag(eventId);
//            }
//            // Do something for lollipop and above versions
//        } else {
//            // URLogs.m("<LOLLIPOP");
//            if (googleMap != null)
//                googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin)).title(Title)).setTag(eventId);
//            // do something for phones running an SDK before lollipop
//        }


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_map_pin_new);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHomeMap:

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                break;
            case R.id.bar_code_bt:

                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Place a barcode inside the rectangle.");
                //integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                //integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.forSupportFragment(this).initiateScan();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                //Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (result != null && result.getContents() != null) {
                    //Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                    if (result.getContents() != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(result.getContents());
                            String appName = jsonObj.getString("app_name");
                            if (AppUtils.isSet(appName) && appName.equals("gcam")) {
                                Integer eventId = jsonObj.getInt("event_id");

                                EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("event_id", eventId.toString());

                                args.putString("isSelfProfile", "mapList");
                                eventDetailsFragment.setArguments(args);
                                ((MainActivity) getActivity()).launchNewFragment(eventDetailsFragment, R.id.container);
                            }
                        } catch (final JSONException e) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    private void setUpMap() {

//        bounds = new LatLngBounds.Builder();
        //get all cars from the datbase with getter method
        final LatLngBounds bounds = googleMap.getProjection()
                .getVisibleRegion().latLngBounds;

        //loop through cars in the database
        for (MapLocationResponse.MapData cn : EventListDataMap) {
            LatLng latLngComp = new LatLng(Double.parseDouble(cn.getEventVenueLat()), Double.parseDouble(cn.getEventVenueLong()));
            i = i + 1;
            // URLogs.m(bounds + ":LatLng Comp:" + latLngComp);
            if (bounds.contains(latLngComp)) {

                //add a map marker for each car, with description as the title using getter methods
                drawMarker(latLngComp, cn.getEventTitle(), cn.getThumbImage(), i, cn.getEventId());

                //use .include to put add each point to be included in the bounds
                //bounds.include(new LatLng(Double.parseDouble(cn.getEventVenueLat()), Double.parseDouble(cn.getEventVenueLong())));
                if (i == 1) {
                    LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                            5);
                    googleMap.moveCamera(update);
                }
            }


        }
        //set bounds with all the map points
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20));
    }


    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void GetEventsPoints() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsMarkerData(new Callback<MapLocationResponse>() {
            @Override
            public void onResponse(Call<MapLocationResponse> call, Response<MapLocationResponse> response) {
                showProgressBar(false);
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        MapFragment.EventListDataMap.clear();
                        MapFragment.EventListDataMap.addAll(response.body().getData());
                        //  setUpMap();
                        if (googleMap != null)
                            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                @Override
                                public void onCameraMove() {
                                    if (handler != null & r != null)
                                        handler.removeCallbacks(r);
                                    handler.postDelayed(r, 600);
                                }
                            });
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<MapLocationResponse> call, Throwable t) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }
}