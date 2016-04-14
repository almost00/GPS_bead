package com.example.pbarn.gps_bead;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mainmenu_Activity extends AppCompatActivity implements ActionBar.TabListener, NavigationView.OnNavigationItemSelectedListener  {

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

        ActionBar ab;
        ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Three tab to display in actionbar
        ActionBar.Tab tab1=ab.newTab();
        tab1.setText("Terkep");
        tab1.setTabListener(this);

        ActionBar.Tab tab2=ab.newTab();
        tab2.setText("Beallitasok");
        tab2.setTabListener(this);


        ab.addTab(tab1, 0, true);
        ab.addTab(tab2, 1, false);

        ab.setDisplayShowTitleEnabled(false);


        //Navigation view példányosítása
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Ezzel tudom bekergetni a NavigationView baloldalról
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        //GPS bekapcsolása
        final TextView gpsBekapcsolasaTextView = (TextView) findViewById(R.id.GpsBekapcsolasTextView);
        gpsBekapcsolasaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beallitasokGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(beallitasokGPSIntent);
            }
        });

        //Adatforgalom bekapcsolása
        final TextView adatforgalomBekapcsolasaTextView = (TextView)findViewById(R.id.AdatforgalomBekapcsolasTextView);
        adatforgalomBekapcsolasaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beallitasokGPSIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(beallitasokGPSIntent);
            }
        });

        //Kattintas animalasa
        final AlphaAnimation kattintas = new AlphaAnimation(1F, 0.8F);
        kattintas.setDuration(1000);
        //Személyes beállításokba átnavigálás
        final TextView adatokTextView = (TextView) findViewById(R.id.SzemelyesBeallitasokTextView);
        adatokTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(kattintas);

                Intent intent = new Intent(v.getContext(), SettingsChange_Activity.class);
                startActivity(intent);
            }
        });

        //ImageView kep beallit a navigation bar-on <-- Kerekített szélekkel
        imageViewKepBeallit(db.felhasznalokKep(), (ImageView) findViewById(R.id.imageView));
    }

    //ImageView kep beallit a navigation bar-on <-- Kerekített szélekkel
    private void imageViewKepBeallit(Bitmap kep, ImageView imageView){

        Bitmap circleBitmap = Bitmap.createBitmap(kep.getWidth(), kep.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(kep,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);

        c.drawCircle(kep.getWidth()/2, kep.getHeight()/2, kep.getWidth()/2, paint);

        imageView.setImageBitmap(circleBitmap);
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





    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        //Called when a tab is selected
        //int nTabSelected = tab.getPosition();
        switch (tab.getText().toString()) {
            case "Terkep":
                break;
            case "Beallitasok":
                Intent intent_ = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.SettingsChange_Activity.class);
                startActivity(intent_);
                break;
           }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

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

                      //  TextView textViewLat = (TextView) findViewById(R.id.textViewLatitude);
                      //  textViewLat.setText("Latitude: " +  latitude + "");

                      //  TextView   textViewLon = (TextView) findViewById(R.id.textViewLongitude);
                      //  textViewLon.setText("Longitude: " + longitude + "");


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



    //----------------NAVIGATION VIEW ----------
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
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






}
