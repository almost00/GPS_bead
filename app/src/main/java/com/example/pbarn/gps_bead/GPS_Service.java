package com.example.pbarn.gps_bead;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GPS_Service extends Service {

    SQLite_Adatbazis db;
    public static final String BROADCAST_ACTION = "android.intent.action.GPS_ADAT";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    @Override
    public void onCreate() {
        super.onCreate();
        db = new SQLite_Adatbazis(this);
        intent = new Intent(BROADCAST_ACTION);
    }


    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        Log.e("GPS_Service_onStart", "Elindult a Service");
    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");


        locationManager.removeUpdates(listener);
    }

    //  public static Thread performOnBackgroundThread(final Runnable runnable) {
    //      final Thread t = new Thread() {
    //          @Override
    //          public void run() {
    //              try {
    //                  runnable.run();
    //              } finally {

    //              }
    //          }
    //      };
    //      t.start();
    //      Log.e("GPS", "Elhalt");
    //      return t;
    //  }


    @Override
    public IBinder onBind(Intent intent) {

        return  null;
    }


    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc) {
            //Ha a pozíció megváltozik beszúrom az adatbázisba, és továbbítom a MainMenuActivity felé broadcast üzenetként.
            Log.i("onLocationChanged", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("LATITUDE", loc.getLatitude());
                intent.putExtra("LONGITUDE", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());

                sendBroadcast(intent);


                //Lekérdezem a felhasználónak az  ID-ját


                Date d = new Date();

                //Ezzel az ID-val szúrom be a GPS_data táblába
                db.InsertRowGPSDATA(loc.getLatitude(), loc.getLongitude(), d.getTime());
            }
        }




        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }



    }
}
