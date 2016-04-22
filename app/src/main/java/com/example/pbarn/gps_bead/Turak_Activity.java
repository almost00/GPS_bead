package com.example.pbarn.gps_bead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Turak_Activity extends AppCompatActivity implements ActionBar.TabListener {


    SQLite_Adatbazis db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turak_);
        db = new SQLite_Adatbazis(this);

        ArrayList<Tura> turaLista = new ArrayList<Tura>();
//
        turaLista.addAll(db.turaListaz());
        if (turaLista.size() > 1) {
            // Create the adapter to convert the array to views
            final TuraAdapter adapter = new TuraAdapter(this, turaLista);
            // Attach the adapter to a ListView
            ListView listViewTura = (ListView) findViewById(R.id.mainListView);
            listViewTura.setAdapter(adapter);

            listViewTura.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tura value = (Tura) adapter.getItem(position);
                    String seged = "";
                    for(int i = 0; i< value.getPozAdatok().size(); i++)
                    {
                        seged +=  value.getPozAdatok().get(i).getLat() + " ";
                        Log.e("LAt", value.pozAdatok.get(i).lat + " " + value.pozAdatok.get(i).time);
                    }
                    Toast.makeText(getApplicationContext(),seged, Toast.LENGTH_LONG).show();

                }
            });
        }




        ActionBar ab;
        ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        ActionBar.Tab tab1 = ab.newTab();
        tab1.setText("Terkep");
        tab1.setTabListener(this);

        ActionBar.Tab tab2 = ab.newTab();
        tab2.setText("Beallitasok");
        tab2.setTabListener(this);

        ActionBar.Tab tab3 = ab.newTab();
        tab3.setText("Turak");
        tab3.setTabListener(this);

        ab.addTab(tab1, 0, false);  //Trze miatt ez lesz a Selected alapértelmezetten
        ab.addTab(tab2, 1, false);
        ab.addTab(tab3, 2, true);

        ab.setDisplayShowTitleEnabled(false);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        switch (tab.getText().toString()) {
            case "Terkep":
                Intent intent_ = new Intent(this.getApplicationContext(), Mainmenu_Activity.class);
                startActivity(intent_);
                break;
            case "Beallitasok":
                Intent intent = new Intent(this.getApplicationContext(), SettingsChange_Activity.class);
                startActivity(intent);
                break;
            case "Turak":
                break;

        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    //Adapter beállítása a ListViewhoz
    public class TuraAdapter extends ArrayAdapter<Tura> {
        public TuraAdapter(Turak_Activity context, ArrayList<Tura> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Pozíció alapján elem meghatározása
            Tura tura = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_elem, parent, false);
            }
            // Lookup view for data population
            TextView turazon = (TextView) convertView.findViewById(R.id.textTuraAzon);


            // Populate the data into the template view using the data object
            turazon.setText(tura.getTuraAzon());

            // Return the completed view to render on screen
            return convertView;
        }


    }
}
