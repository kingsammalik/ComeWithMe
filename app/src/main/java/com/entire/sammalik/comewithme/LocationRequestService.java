package com.entire.sammalik.comewithme;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class LocationRequestService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context context;
    public static ArrayList<LatLng> points = new ArrayList<LatLng>();
    double plat=0,plong=0;
    static double  dis=0;
    int count=0;
   // dbhelper dbhelper;
    static float speed;
    //Stopwatch timer = new Stopwatch();

    private static final long INTERVAL = 2000 ;//10 sec
    private static final long FASTEST_INTERVAL = 1000; // 5 sec
    private double latitude, longitude;

   static SamLocationListener samLocationListener;
     LocationRequestService(Context context) {
        this.context=context.getApplicationContext();


    }

    public LocationRequestService() {
    }

    public static void getsamlocationlistener( SamLocationListener samLocationListener){
        LocationRequestService.samLocationListener=samLocationListener;
    }

    public void executeService() {
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d("", "Location update resumed .....................");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface SamLocationListener {

        public void onLocationUpdate(Location location);

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("helper","accuracy "+location.getAccuracy());

        int MIN_ACCURACY = 25; // in metters
        if ((!location.hasAccuracy()) || (location.getAccuracy() > MIN_ACCURACY))
        {
            Toast.makeText(this,"Discarding inaccurate location",Toast.LENGTH_SHORT).show();
        }
        else {
            if(count==0){
                plat=location.getLatitude();;
                plong=location.getLongitude();;
            }
            else {
                plat=latitude;
                plong=longitude;
            }
            count++;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            double distemp= getDistance(plat, plong, latitude, longitude);
            distemp = Math.round(distemp * 100);
            distemp = distemp/100;
            dis=dis+distemp;
             dis = BigDecimal.valueOf(dis)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            //notification();
            points.add(position);
            Log.e("helper","dis "+dis);
           /* String[] a=dbhelper.getLatLong();
            plat=Double.parseDouble(a[0]);
            plong=Double.parseDouble(a[1]);
            dis= getDistance(plat, plong, latitude, longitude);
            dis = Math.round(dis * 100);
            dis = dis/100;
            Log.e("helper","distance "+dis);
            Log.e("helper","time "+count);
            dbhelper.setLatLong(latitude,longitude);
            float speed = (float) dis/count;
            DecimalFormat df = new DecimalFormat("#.00");
            df.format(speed);
            Log.e("helper","speed "+ location.getSpeed());*/
            samLocationListener.onLocationUpdate(location);
        }

       /* if ((String.valueOf(location.getLatitude())).equals(null)) {

        } else {
            stopLocationUpdates();

        }*/
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        if(lat1==0){
            lat1=lat2;
            lon1=lon2;
        }
       /* double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang *6371;
        return dist;*/
        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);

        Location locationB = new Location("point B");

        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        float[] a=new float[1];
        Location.distanceBetween( lat1,  lon1,  lat2,  lon2,a);
        // float distance = locationA.distanceTo(locationB);
        float distance=a[0];
        return distance;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d("LocationRequestService", "Location update started ..............: ");

    }




    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d("LocationRequestService", "Location update stopped .......................");
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //  mLocationRequest.setSmallestDisplacement(20);
        Stopwatch.start();
        handlerfunction();
        executeService();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        Intent notificationIntent = new Intent(this, MapsHomeActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
       // contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
       // contentView.setTextViewText(R.id.title, "Custom notification");
       // contentView.setTextViewText(R.id.text, "This is a custom layout");
        notification.contentView = contentView;

       // Intent notificationIntent = new Intent(this, MapsHomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        // notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound

      /*  Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Members")
                .setTicker("C4U")
                .setContentText("Calculating distance")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();*/
       /* notification.setLatestEventInfo(getApplicationContext(), getText(R.string.app_name),
                "Calculating distance", pendingIntent);*/
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        Stopwatch.stop();
        stopSelf();
        stopLocationUpdates();
        //locationRequestService.stopLocationUpdates();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

     void notification(){
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        //contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
         int seconds = (int) (Stopwatch.getElapsedTime() / 1000) % 60 ;
         int minutes = (int) ((Stopwatch.getElapsedTime() / (1000*60)) % 60);
         //Log.e("dsh","mmins "+minutes);
         int hours   = (int) ((Stopwatch.getElapsedTime() / (1000*60*60)) % 24);

        contentView.setTextViewText(R.id.text, String.format("%02d:%02d:%02d",hours ,minutes
                ,seconds ));
        contentView.setTextViewText(R.id.distance,Html.fromHtml(dis+" <sup><small>" + "M" + "</small></sup>"));
        contentView.setTextViewText(R.id.title, Html.fromHtml(speed+" <sup><small>" + "MPS" + "</small></sup>") );
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, MapsHomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

       // notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        //notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        //notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        mNotificationManager.notify(1, notification);
    }

    void handlerfunction(){
        final Handler h = new Handler();
        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){

                //do something
                if(isMyServiceRunning(LocationRequestService.class)){
                    notification();
                    h.postDelayed(this, delay);
                }

            }
        }, delay);
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

    public static long getTimeElapsed(){
        return Stopwatch.getElapsedTime();
    }

}