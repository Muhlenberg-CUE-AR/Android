package muhlenberg.edu.cue.services;

import android.graphics.Point;
import android.util.Log;

import muhlenberg.edu.cue.util.location.CUELocation;

/**
 * Created by Willy on 2/20/2017.
 * Jalal is lame
 */

public class Building {
    // information for each building
    private int id;
    private String name;
    private String shortDesc;
    private String longDesc;
    private Float lat;
    private Float lon;
    private CUELocation activationBoxNE;
    private CUELocation activationBoxNW;
    private CUELocation activationBoxSE;
    private CUELocation activationBoxSW;

    public Building(int id, String name, String shortDesc, String longDesc, Float lat, Float lon){
        // sets all fields given through the constructor
        this.id = id;
        this.name = name;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.lat = lat;
        this.lon = lon;
        /*this.activationBoxNE = activationBoxNE;
        this.activationBoxNW = activationBoxNW;
        this.activationBoxSE = activationBoxSE;
        this.activationBoxSW = activationBoxSW;*/
    }

    // sets the lat, lng for the activation box
    public void addActivationBoxCoordinate(CUELocation ne, CUELocation nw, CUELocation se, CUELocation sw){
        this.activationBoxNE = ne;
        this.activationBoxNW = nw;
        this.activationBoxSE = se;
        this.activationBoxSW = sw;
    }

    // getter methods
    // no setters because the data will not change
    public int getId() { return this.id; }

    public String getName() { return this.name; }

    public String getShortDesc() { return this.shortDesc; }

    public String getLongDesc() { return this.longDesc; }

    public Float getLat() { return this.lat; }

    public Float getLon() { return this.lon; }

}
