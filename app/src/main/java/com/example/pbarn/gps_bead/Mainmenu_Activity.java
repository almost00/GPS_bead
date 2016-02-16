package com.example.pbarn.gps_bead;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Mainmenu_Activity extends FragmentActivity {

    MyReceiver myReceiver;
    GoogleMap googleMap;
    SQLite_Adatbazis db;

    //  public void onMapReady(GoogleMap map) {
    //      googleMap = map;

    //      // Add a marker in Sydney and move the camera
    //      LatLng sydney = new LatLng(-34, 151);
    //      googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    //      googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    //  }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu_);

        // GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();


        db = new SQLite_Adatbazis(this);
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.GPS_ADAT");
        registerReceiver(myReceiver, intentFilter); //felregisztrálás
    }


    @Override
    protected void onStart() {
        super.onStart();

        if(!isMyServiceRunning(GPS_Service.class))
        {
            Intent intent = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.GPS_Service.class);
            startService(intent);
        }
    }
    //Csekkolom az éppen futó service-eket, ha fut a paraméterben beadott, akkor true értékkel visszatér.
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Beállítások módosítása fül előhívásához.
    public void settingsChangeActivityInditasaOnClick(View view)
    {
        Intent intent = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.SettingsChange_Activity.class);
        startActivity(intent);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }


    //Elkapjuk, és kiíratjuk a Lat, és Lon adatokat, amiket a GPS_Service küld.
    public class MyReceiver extends BroadcastReceiver {
        static final String TAG = "android.intent.action.GPS_ADAT";

        @Override
        public void onReceive(Context arg0, final Intent arg1) {
            if(arg1.getAction() == TAG) {
                //UI ra kiiratás szálból, másképp nem lehet
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = arg1.getExtras();
                        final double longitude = bundle.getDouble("LONGITUDE");
                        final double latitude = bundle.getDouble("LATITUDE");
                        Log.e("Longitude", "Longitude: " + longitude);
                        Log.e("Latitude", "Latitude: " + latitude);

                        TextView textViewLat = (TextView) findViewById(R.id.textViewLatitude);
                        textViewLat.setText("Latitude: " +  latitude + "");

                        TextView   textViewLon = (TextView) findViewById(R.id.textViewLongitude);
                        textViewLon.setText("Longitude: " + longitude + "");


                        //Aktuális pozíciónak jelölő marker kitétele a térképre.
                        googleMap.clear();
                        LatLng aktPoz = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(aktPoz).title("Marker in Sydney"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(aktPoz));
                    }
                });
            }
        }

    }







}
