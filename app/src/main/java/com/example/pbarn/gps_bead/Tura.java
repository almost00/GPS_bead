package com.example.pbarn.gps_bead;

import java.util.ArrayList;

/**
 * Created by pbarn on 2016. 04. 22..
 */
public class Tura {

    String turaAzon;
    String turaDatum;
    ArrayList<PozAdatok> pozAdatok;

    public String getTuraAzon() {
        return turaAzon;
    }

    public void setTuraAzon(String turaAzon) {
        this.turaAzon = turaAzon;
    }

    public String getTuraDatum() {
        return turaDatum;
    }

    public void setTuraDatum(String turaDatum) {
        this.turaDatum = turaDatum;
    }

    public ArrayList<PozAdatok> getPozAdatok() {
        return pozAdatok;
    }

    public void setPozAdatok(ArrayList<PozAdatok> pozAdatok) {
        this.pozAdatok = pozAdatok;
    }

    public Tura(String turaAzon, String turaDatum, ArrayList<PozAdatok> pozAdatok) {

        this.turaAzon = turaAzon;
        this.turaDatum = turaDatum;
        this.pozAdatok = pozAdatok;
    }
    public Tura() {

    }
}
