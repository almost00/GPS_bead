package com.example.pbarn.gps_bead;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SettingsChange_Activity extends AppCompatActivity implements ActionBar.TabListener {

    SQLite_Adatbazis db;
    int felhasznalo_ID;
    String felhasznalonevRegi;
    String jelszoRegi;
    String emailRegi;
    String kivalasztottKepBase64Regi;

    String kivalasztottKepBase64Uj;
    String felhasznalonevUj;
    String jelszoUj;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_);


        db = new SQLite_Adatbazis(this);
        Log.e("SettingsChange_Activity", "Elindult a SettingsChange_Activity");

        ArrayList<String> settingsAdatok = db.getSettingsAdatok();
        felhasznalonevRegi = settingsAdatok.get(0);
        jelszoRegi = settingsAdatok.get(1);
        emailRegi = settingsAdatok.get(3);
        kivalasztottKepBase64Regi = settingsAdatok.get(2);
        felhasznalo_ID = Integer.parseInt(settingsAdatok.get(4));


        felhasznalonevUj = felhasznalonevRegi;
        jelszoUj = jelszoRegi;
        kivalasztottKepBase64Uj = kivalasztottKepBase64Regi;


        //Betöltöm a már beadott adatokat a megfelelő szövegmezőkbe/imageView-ba.
        alapBeallitasok(felhasznalonevUj, jelszoUj, KepMuveletek.decodeBase64(kivalasztottKepBase64Uj), emailRegi);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Three tab to display in actionbar
        ActionBar.Tab tab1=ab.newTab();
        tab1.setText("Terkep");
        tab1.setTabListener(this);

        ActionBar.Tab tab2=ab.newTab();
        tab2.setText("Beallitasok");
        tab2.setTabListener(this);

        ActionBar.Tab tab3=ab.newTab();
        tab3.setText("Turak");
        tab3.setTabListener(this);

        ab.addTab(tab1, 0, false);  //Trze miatt ez lesz a Selected alapértelmezetten
        ab.addTab(tab2, 1, true);
        ab.addTab(tab3, 2, false);

        ab.setDisplayShowTitleEnabled(false);
    }


    //Felhasználónév megváltoztatásához
    public void FelhasznalonevModositasOnClick(View view) {
        Log.e("OnClick","FelhasznalonevModositasOnClick");
        setContentView(R.layout.settings_change_felhasznalonev_modositas);
        TextView textViewFelhasznalonevRegi = (TextView)findViewById(R.id.textViewSettingsChangeFelhasznalonevModositas);
        textViewFelhasznalonevRegi.setText(felhasznalonevRegi);

    }

    public void SettingsChangeFelhasznalonevModositasMentesOnclick(View view) {
        EditText editTextFelhasznalonev = (EditText) findViewById(R.id.editTextSettingsChangeFelhasznalonevModositas);

        if (editTextFelhasznalonev.getText().toString() != "") {
            Log.e("Onclick", "SettingsChangeFelhasznalonevModositasMentesOnclick");
            setContentView(R.layout.activity_settings_change_);

            felhasznalonevUj = editTextFelhasznalonev.getText().toString();
            alapBeallitasok(felhasznalonevUj,jelszoUj,KepMuveletek.decodeBase64(kivalasztottKepBase64Uj),emailRegi);
        }
        setContentView(R.layout.activity_settings_change_);
        alapBeallitasok(felhasznalonevUj, jelszoUj, KepMuveletek.decodeBase64(kivalasztottKepBase64Uj), emailRegi);
    }
    public void SettingsChangeFelhasznalonevModositasMegseOnclick(View view)
    {
        setContentView(R.layout.activity_settings_change_);
        alapBeallitasok(felhasznalonevUj, jelszoUj, KepMuveletek.decodeBase64(kivalasztottKepBase64Uj), emailRegi);
    }


//Jelszó megváltoztatásához

    public void JelszoModositasOnClick(View view) {
        setContentView(R.layout.settings_change_jelszo_modositas);
    }


    public void SettingsChangeJelszoModositasMentesOnclick(View view) {
        EditText regiJelszo = (EditText)findViewById(R.id.editTextSettingsChangeJelszoModositasRegiJelszo);
        EditText ujjelszo1 = (EditText)findViewById(R.id.editTextSettingsChangeJelszoModositasUjJelszo_1);
        EditText ujjelszo2 = (EditText)findViewById(R.id.editTextSettingsChangeJelszoModositasUjJelszo_2);

        //Az első editText-be beadott jelsz a régi jelszó?
        if(sha1Sajat(regiJelszo.getText().toString().trim()).equals(jelszoRegi) )
        {
            //masik vizsgal
            if (ujjelszo1.getText().toString().trim().equals(ujjelszo2.getText().toString().trim()) && ujjelszo1.getText().toString() != "" ){

                jelszoUj=  sha1Sajat(ujjelszo1.getText().toString());
                setContentView(R.layout.activity_settings_change_);
                alapBeallitasok(felhasznalonevUj,jelszoUj,KepMuveletek.decodeBase64(kivalasztottKepBase64Uj),emailRegi);
            }
            else{
                Toast.makeText(this, "A két új jelszó nem egyezik!", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, "Hibás régi jelszó!", Toast.LENGTH_LONG).show();
        }
    }

    public void SettingsChangeJelszoModositasMegseOnclick(View view) {
        setContentView(R.layout.activity_settings_change_);
        alapBeallitasok(felhasznalonevUj, jelszoUj, KepMuveletek.decodeBase64(kivalasztottKepBase64Uj), emailRegi);
    }



    //Képcsere
    private static final int SELECT_PHOTO = 100;
    public void  settings_change_Kepcsere(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Log.e("SWITCH", "SWITCH");
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    //Átméretezzük a képet 100x100 assá
                    yourSelectedImage = KepMuveletek.getResizedBitmap(yourSelectedImage, 100, 100);

                    Log.e("SWITCH_VEGE", "SWITCH_VEGE");
                    //Eltarolom kulso valtozoban a kivalsztott, és már átméretezett
                    kivalasztottKepBase64Uj = KepMuveletek.encodeTobase64(yourSelectedImage);

                    //Kép betöltése az imageview-ba
                    ImageView imageViewKep = (ImageView) findViewById(R.id.imageViewSettingsChangeKep);
                    imageViewKep.setImageBitmap(yourSelectedImage);


                }
        }
    }



    public String sha1Sajat(String jelszo){

        //SHA-1-el HASH-elem a jelszót----------------------------------------------------------------
        final MessageDigest digest;
        byte[] result = new byte[0];
        try {
            digest = MessageDigest.getInstance("SHA-1");
            result = digest.digest(jelszo.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Another way to make HEX, my previous post was only the method like your solution
        StringBuilder sb = new StringBuilder();

        for (byte b : result) // Bejárom a result tömböt.
        {
            sb.append(String.format("%02X", b));
        }
        String messageDigest = sb.toString(); //Sha-1el hash-elve a jelszó

        return messageDigest;
    }

    public void alapBeallitasok(String felhasznalonev, String jelszo, Bitmap kep, String email)
    {
        //Betöltöm a már beadott adatokat a megfelelő szövegmezőkbe/imageView-ba.
        TextView  felhasznalonevTextview = (TextView) findViewById(R.id.textViewSettingsChangeFelhasznalonev);
        felhasznalonevTextview.setText("Felhasználónév:" + felhasznalonev);

        TextView  jelszoTextview = (TextView) findViewById(R.id.textViewSettingsChangeJelszo);
        jelszoTextview.setText("Jelszó: " + jelszo);

        ImageView imageViewKep = (ImageView) findViewById(R.id.imageViewSettingsChangeKep);
        imageViewKep.setImageBitmap(kep);

        TextView emailTextview = (TextView) findViewById(R.id.textViewSettingsChangeEmail);
        emailTextview.setText("Emailcím: " + email);
    }


    public void VisszaSettingsActivityOnClick(View view) {
        //Ha valamelyik adatban változás történt akkor postolom a web felé
        if(felhasznalonevRegi != felhasznalonevUj || jelszoUj != jelszoRegi || kivalasztottKepBase64Uj != kivalasztottKepBase64Regi )
        {
            //Az adatbázisban frissítjük az adott bejegyzést az új adatokkal.
            db.updateSettingsRow(felhasznalo_ID,felhasznalonevUj,jelszoUj,kivalasztottKepBase64Uj);
        }

        //Vissza a MainMenuActivitybe
        Intent intent = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.Mainmenu_Activity.class);
        startActivity(intent);
    }




    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        //Called when a tab is selected
        //int nTabSelected = tab.getPosition();
        switch (tab.getText().toString()) {
            case "Terkep":
                Intent intent_ = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.Mainmenu_Activity.class);
                startActivity(intent_);
                break;
            case "Beallitasok":
                break;
            case "Turak":
                Intent intent = new Intent(this.getApplicationContext(),Turak_Activity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}