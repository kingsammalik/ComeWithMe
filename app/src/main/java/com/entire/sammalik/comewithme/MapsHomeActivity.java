package com.entire.sammalik.comewithme;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MapsHomeActivity extends FragmentActivity implements OnMapReadyCallback, LocationRequestService.SamLocationListener, View.OnClickListener {

    private GoogleMap mMap;
    boolean firstTime = false,ismapready=false;
    //PolylineOptions polyLineOptions = null;
    // ArrayList<LatLng> points = null;
    Polyline line;
    long time;
    double latitude, longitude;
    TextView speedtxt;
    //float speed;
    //double distance;
    LinearLayout startjourney, stopjourney;
    //LocationRequestService locationRequestService;
    private GoogleApiClient googleApiClient;
    //Stopwatch timer = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_maps_home);
        startjourney = (LinearLayout) findViewById(R.id.startjourney);
        startjourney.setOnClickListener(this);
        stopjourney = (LinearLayout) findViewById(R.id.stopjourney);
        stopjourney.setOnClickListener(this);
        // points = new ArrayList<LatLng>();
        speedtxt = (TextView) findViewById(R.id.speed);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        stopjourney.setEnabled(false);
        stopjourney.setAlpha(.3f);
        startjourney.setEnabled(false);
        startjourney.setAlpha(.3f);
        Toast.makeText(MapsHomeActivity.this,"Please wait fetching the World for you!!!!!",Toast.LENGTH_SHORT).show();
        //notification();
        if (isMyServiceRunning(LocationRequestService.class)) {
            startjourney.setEnabled(false);
            startjourney.setAlpha(.3f);
            stopjourney.setEnabled(true);
            stopjourney.setAlpha(1f);
            // Permission Granted
            //locationRequestService = new LocationRequestService(MapsHomeActivity.this);
            LocationRequestService.getsamlocationlistener(this);
            turnongps();
            handlerfunction();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission Granted
                    //locationRequestService = new LocationRequestService(MapsHomeActivity.this);
                    //mMap.setMyLocationEnabled(true);
                    LocationRequestService.getsamlocationlistener(this);
                    turnongps();
                    startService(new Intent(MapsHomeActivity.this, LocationRequestService.class));

                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ismapready=true;
        mMap.setBuildingsEnabled(true);
        if (isMyServiceRunning(LocationRequestService.class)) {

        }
        else {
            startjourney.setEnabled(true);
            startjourney.setAlpha(1f);
        }

       // mMap.setTrafficEnabled(true);


        // Add a marker in Sydney and move the camera

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstTime=false;
    }

    @Override
    public void onLocationUpdate(Location location) {
        //this.time=time;
        //this.distance=distance;
        //speed=location.getSpeed();
      //  polyLineOptions = new PolylineOptions();
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        //Log.e("map","lat "+location.getLatitude()+" long "+location.getLongitude());
        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));

        if((!firstTime)&& ismapready){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            firstTime=true;
        }

        //LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        //points.add(position);
        redrawLine(LocationRequestService.points); //added
        /*polyLineOptions.addAll(points);
        polyLineOptions.width(2);
        polyLineOptions.color(Color.BLUE);
        mMap.addPolyline(polyLineOptions);*/
    }
    private void redrawLine(ArrayList<LatLng> points) {
        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
        line = mMap.addPolyline(options); //add Polyline
    }



    @Override
    public void onClick(View view) {
        int id=view.getId();
         if(id==R.id.startjourney){
             startjourney.setEnabled(false);
             startjourney.setAlpha(.3f);
             stopjourney.setEnabled(true);
             stopjourney.setAlpha(1f);

             handlerfunction();
             ActivityCompat.requestPermissions(MapsHomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                     123);

         }
        if(id==R.id.stopjourney){
            stopjourney.setEnabled(false);
            stopjourney.setAlpha(.3f);
            stopService(new Intent(MapsHomeActivity.this, LocationRequestService.class));
        }
    }

    void turnongps(){
          if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);


            builder.setAlwaysShow(true);

            PendingResult result = LocationServices.SettingsApi .checkLocationSettings(googleApiClient, builder.build());


            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode())
                    {

                        case LocationSettingsStatusCodes.SUCCESS:

                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MapsHomeActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e)
                            {
                                e.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;

                    }
                }
            });
        }
        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    void handlerfunction(){
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
//                Log.e("map","time frm service "+locationRequestService.getTimeElapsed());
                int seconds = (int) (LocationRequestService.getTimeElapsed() / 1000) % 60 ;
                int minutes = (int) ((LocationRequestService.getTimeElapsed() / (1000*60)) % 60);
                //Log.e("dsh","mmins "+minutes);
                int hours   = (int) ((LocationRequestService.getTimeElapsed() / (1000*60*60)) % 24);
                speedtxt.setText(Html.fromHtml(LocationRequestService.speed+" <sup><small>" + "MPS" + "</small></sup>"));
                ((TextView)findViewById(R.id.time)).setText(String.format("%02d:%02d:%02d",hours ,minutes
                        ,seconds ));
                ((TextView)findViewById(R.id.distance)).setText(Html.fromHtml(LocationRequestService.dis+" <sup><small>" + "M" + "</small></sup>"));
                //do something
                if(isMyServiceRunning(LocationRequestService.class)){

                    h.postDelayed(this, delay);
                }

            }
        }, delay);
    }
}
