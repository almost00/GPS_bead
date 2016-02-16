package com.example.pbarn.gps_bead;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by pbarn on 2016. 01. 16..
 */
public class SQLite_Adatbazis extends SQLiteOpenHelper {

    private static SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GPS_DATABASE";

    private static final String TABLE_SETTINGS = "Settings";  //ID, Név,jelszó,email, kép
    private static final String TABLE_GPSDATA = "GpsData";  //ID,  Lat, Lon, Timestamp

    //SETTINGS tábla
    private static final String SETTINGS_ID = "ID";
    private static final String SETTINGS_EMAIL = "Email";
    private static final String SETTINGS_NEV = "Nev";
    private static final String SETTINGS_JELSZO = "Jelszo";
    private static final String SETTINGS_KEP = "Kep";

    //GPS_DATA tábla
    private static final String GPSDATA_ID = "ID";
    private static final String GPSDATA_LONGITUDE = "Longitude";
    private static final String GPSDATA_LATITUDE = "Latitude";
    private static final String GPSDATA_TIMESTAMP = "Timestamp";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE if not EXISTS " + TABLE_SETTINGS
            + "(" + SETTINGS_ID + " INTEGER PRIMARY KEY," + SETTINGS_NEV + " VARCHAR," + SETTINGS_JELSZO + " VARCHAR,"
            + SETTINGS_EMAIL + " VARCHAR," +SETTINGS_KEP + " VARCHAR" + ")";

    private static final String CREATE_TABLE_GPSDATA = "CREATE TABLE if not EXISTS " + TABLE_GPSDATA
            + "(" + GPSDATA_ID + " INTEGER PRIMARY KEY,"+  GPSDATA_LONGITUDE + " DOUBLE,"
            + GPSDATA_LATITUDE + " DOUBLE,"+ GPSDATA_TIMESTAMP + " DATETIME" + ")";


    public SQLite_Adatbazis(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getWritableDatabase();
        db.execSQL(CREATE_TABLE_SETTINGS);
        Log.e("CREATE_TABLE_SETTINGS", "CREATE_TABLE_SETTINGS");
        db.execSQL(CREATE_TABLE_GPSDATA);
        Log.e("CREATE_TABLE_GPSDATA", "CREATE_TABLE_GPSDATA");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPSDATA);
    }


    public  void InsertRowGPSDATA( double longitide, double latitude, long timestamp)
    {
        Log.e("InsertRowGPSDATA", "Sikeres beszúrás:" +"| Lon: " + longitide + "| Lat: " + latitude + "| Timestamp: " + timestamp);
        db.execSQL("Insert into GpsData values(null," + longitide + "," + latitude + "," + timestamp + " );");
    }

    public  void InsertRowSETTINGS(String nev, String jelszo, String email,  String kep)
    {    //ID, Név, jelszó, email, kép
        Log.e("InsertRowSETTINGS", "Sikeres beszúrás: Név:"+nev + "| Jelszo: "+ jelszo+ "| Email: " +email +"| kep: "+ kep);
        db.execSQL("Insert into Settings values("+ 1 + ",'"+ nev +"','"+jelszo+"','"+ email +"','"+kep +"' );");
    }

    public void  TruncateSETTINGS()
    {
        db.execSQL("DELETE FROM Settings;");
        db.execSQL("VACUUM;");
    }

    // public  String  SelectAllGPSDATA()
    // {
    //     Cursor c = db.rawQuery("Select * From GpsData Order by ID DESC",null, null);
    //     if(c.moveToFirst())
    //     {
    //         return c.getString(c.getColumnIndex("Longitude"));
    //     }
    //     return  "";
    // }



    public  ArrayList<String> getSettingsAdatok()
    {
        Cursor b = db.rawQuery("Select * From Settings", null, null);
        ArrayList<String> adatok = new ArrayList<String>();
        if(b.moveToLast()){
            adatok.add(b.getString(b.getColumnIndex("Nev")));
            adatok.add(b.getString(b.getColumnIndex("Jelszo")));
            adatok.add(b.getString(b.getColumnIndex("Kep")));
            adatok.add(b.getString(b.getColumnIndex("Email")));
            adatok.add(b.getString(b.getColumnIndex("ID")));

            //Ellenorzes
            Log.e("getSettingsAdatok", "Lekérdezés eredménye: Név:" + adatok.get(0) + "| Jelszo: " + adatok.get(1) + "| Email: " + adatok.get(3) + "| kep: " + adatok.get(2));

            b.close();
            return adatok;
        }
        b.close();
        return null;
    }

    public  void updateSettingsRow(int id, String nev, String jelszo, String kep) {

        ContentValues cv = new ContentValues();
        cv.put(SETTINGS_NEV,nev); //These Fields should be your String values of actual column names
        cv.put(SETTINGS_JELSZO,jelszo);
        cv.put(SETTINGS_KEP, kep);
        db.update(TABLE_SETTINGS, cv, SETTINGS_ID + "=" + id, null);

        Log.e("updateSettingsRow", cv.toString());


    }


}