package com.example.pbarn.gps_bead;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Settings_Activity extends AppCompatActivity {

    SQLite_Adatbazis db ;
    String kivalasztottKepBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        if (db == null) {
            db = new SQLite_Adatbazis(this); //SQLiteDB példányosítás
        }
        //Ha már regisztráltak akkor ne ez az activity jelenjen meg.
        if (db.getSettingsAdatok() != null){
            startMainActivity();
        }

    }

    private static final int SELECT_PHOTO = 100;
    //Eseménykezelő: ImageViewKep, és KepCsere gombhoz rendelve
    public void settings_Kepcsere(View view)
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
                    kivalasztottKepBase64 = KepMuveletek.encodeTobase64(yourSelectedImage);

                    //Kép betöltése az imageview-ba
                    ImageView imageViewKep = (ImageView) findViewById(R.id.imageViewKep);
                    imageViewKep.setImageBitmap(yourSelectedImage);
                }
        }
    }



    //Eseménykezelő: A bepötyörészett adatokat elmentem egy adatbázisba. Majd továbbítom a webszerver felé.
    public void  OnClickSettingsMentes(View view) {
        EditText _name = (EditText)findViewById(R.id.textViewNev);
        EditText _password = (EditText) findViewById(R.id.textViewJelszo);
        EditText _passwordmegint = (EditText)findViewById(R.id.textViewtextViewJelszomegint);
        EditText _email = (EditText)findViewById(R.id.textViewEmail);

        adatEll(_name.getText().toString(), _password.getText().toString(), _passwordmegint.getText().toString(), _email.getText().toString());

    }


    public void adatEll(String name, String password, String passwordmegint, String email)
    {
        //A beadott jelszavak egyezőségét vizsgálom.
        if (!password.equals(passwordmegint))
        {
            Toast.makeText(this, "A megadott jelszó nem egyezik. Kérlek pötyögd be újra.", Toast.LENGTH_LONG).show();
        }
        else {

            String passwordSha1 = Sha1Sajat(password); //Sha-1el hash-elve a jelszó
            Log.e("SHA1", passwordSha1);

            //Megnézzük kitöltötte e az adatmezőket.
            if (name.length() > 0 && password.length() > 0 && email.length() > 0) {

                // Megvizsgálom, hogy érvényes mail címet adott e meg a felhasználó.
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Log.e("OnClickSettingsMentes", "A beadott adatok érvésnyesek");
                    //Ha mindent beadott helyesen akkor beszúrom a db-be

                    db.InsertRowSETTINGS(name, password, email, kivalasztottKepBase64);
                    startMainActivity();
                }
                else
                {
                    Toast.makeText(this, "Kérek érvényes e-mail címet adjál meg.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Kérlek minden adatmezőt töltsél ki.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String Sha1Sajat(String jelszo){

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
        Log.e("SHA1", sb.toString());
        return sb.toString(); //Sha-1el hash-elve a jelszó


    }


    //Mielőtt elindítom ellenőrizni kell, hogy a beadott adatok helyes e.
    public void startMainActivity()
    {
        Intent intent = new Intent(this.getApplicationContext(), com.example.pbarn.gps_bead.Mainmenu_Activity.class);
        startActivity(intent);
    }



}



/*
package com.example.pbarn.gps_mobil;

        import android.content.Context;
        import android.view.GestureDetector;
        import android.view.GestureDetector.SimpleOnGestureListener;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnTouchListener;

/**
 * Created by pbarn on 2016. 02. 15..
 */
/*
public abstract class SwipeEsemenyKezelo implements View.OnTouchListener {


    //Csúsztatásra történik az activityk közötti váltás
    private final GestureDetector kezmozdulatDetektor;

    public SwipeEsemenyKezelo (Context ctx){
        kezmozdulatDetektor = new GestureDetector(ctx, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return kezmozdulatDetektor.onTouchEvent(event);
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_KUSZOBERTEK = 100;
        private static final int SWIPE_SEBESSEG_KUSZOB = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }



        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  //velocityX : pixel/sec értéket ad vissza
            boolean result = false;
            try {
                float Y_tegelyMentiElmozdulas = e2.getY() - e1.getY();
                float X_tegelyMentiElmozdulas = e2.getX() - e1.getX();
                //Ha oldalirányú mozgás történt.
                if (Math.abs(X_tegelyMentiElmozdulas) > Math.abs(Y_tegelyMentiElmozdulas)) {
                    //Ha a mozgás a megadott csúszás köszöbörték, és sebességérték felett volt akkor lefut a kívánt Swipe metódus
                    if (Math.abs(X_tegelyMentiElmozdulas) > SWIPE_KUSZOBERTEK && Math.abs(velocityX) > SWIPE_SEBESSEG_KUSZOB) {
                        if (X_tegelyMentiElmozdulas > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}

*/
