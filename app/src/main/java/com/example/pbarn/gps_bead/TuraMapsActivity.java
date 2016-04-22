package com.example.pbarn.gps_bead;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class TuraMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Tura aktTura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tura_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


         aktTura =(Tura)getIntent().getSerializableExtra("Tura");



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

        // Add a marker in Sydney and move the camera
       //LatLng sydney = new LatLng(-34, 151);
       //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < aktTura.getPozAdatok().size(); z++) {
            LatLng point = new LatLng(aktTura.getPozAdatok().get(z).getLat(), aktTura.getPozAdatok().get(z).getLon());
            options.add(point);

        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(options.getPoints().get(0)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        mMap.addPolyline(options);
    }
}
