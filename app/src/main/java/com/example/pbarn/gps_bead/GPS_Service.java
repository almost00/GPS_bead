package com.example.pbarn.gps_bead;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;


public class GPS_Service extends Service {

    SQLite_Adatbazis db;
    public static final String BROADCAST_ACTION = "android.intent.action.GPS_ADAT";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private int turaID;

    Intent intent;
    @Override
    public void onCreate() {
        super.onCreate();
        db = new SQLite_Adatbazis(this);
        intent = new Intent(BROADCAST_ACTION);
    }


    @Override
    public void onStart(Intent intent, int startId) {
        if(intent != null){
            turaID = intent.getIntExtra("TuraAzonosito", -1);
        }

        Log.e("SERVICE_TURA_AZON","S_TURA: " + turaID);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        Toast.makeText(getApplicationContext(), "START_SERVICE", Toast.LENGTH_SHORT).show();
        Log.e("GPS_Service_onStart", "Elindult a Service");
    }


    protected boolean vanJobbLocation(Location location, Location aktualisLegjobbLocation) {
        if (aktualisLegjobbLocation == null) {
            return true;
        }

        long idoDelta = location.getTime() - aktualisLegjobbLocation.getTime();
        boolean jelentosenUjabb = idoDelta > TWO_MINUTES;
        boolean jelentosenRegebbi = idoDelta < -TWO_MINUTES;
        boolean ujabb = idoDelta > 0;

        if (jelentosenUjabb) {
            return true;
        } else if (jelentosenRegebbi) {
            return false;
        }

        int pontossagDelta = (int) (location.getAccuracy() - aktualisLegjobbLocation.getAccuracy());
        boolean kevesbePontos = pontossagDelta > 0;
        boolean pontosabb = pontossagDelta < 0;
        boolean legkevesbePontos = pontossagDelta > 200;

        boolean ugyanattolaProvidertol = ugyanattolaProvidertol(location.getProvider(),
                aktualisLegjobbLocation.getProvider());

        if (pontosabb) {
            return true;
        } else if (ujabb && !kevesbePontos) {
            return true;
        } else if (ujabb && !legkevesbePontos && ugyanattolaProvidertol) {
            return true;
        }
        return false;
    }


    private boolean ugyanattolaProvidertol(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "STOP_SERVICE", Toast.LENGTH_SHORT).show();
        Log.e("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }


    @Override
    public IBinder onBind(Intent intent) {

        return  null;
    }


    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc) {
            //Ha a pozíció megváltozik beszúrom az adatbázisba, és továbbítom a MainMenuActivity felé broadcast üzenetként.
            Log.i("onLocationChanged", "Location changed");
            if (vanJobbLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("LATITUDE", loc.getLatitude());
                intent.putExtra("LONGITUDE", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());

                sendBroadcast(intent);
                Date d = new Date();
                db.InsertRowGPSDATA(turaID, loc.getLatitude(), loc.getLongitude(), loc.getAltitude(),loc.getSpeed(), d.getTime());
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
