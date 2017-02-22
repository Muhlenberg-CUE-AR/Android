package muhlenberg.edu.cue.services;

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
    private String activationBoxNE;
    private String activationBoxNW;
    private String activationBoxSE;
    private String activationBoxSW;

    public Building(int id, String name, String shortDesc, String longDesc, Float lat, Float lon,
                    String activationBoxNE, String activationBoxNW, String activationBoxSE, String activationBoxSW){
        // sets all fields given through the constructor
        this.id = id;
        this.name = name;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
        this.lat = lat;
        this.lon = lon;
        this.activationBoxNE = activationBoxNE;
        this.activationBoxNW = activationBoxNW;
        this.activationBoxSE = activationBoxSE;
        this.activationBoxSW = activationBoxSW;
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
