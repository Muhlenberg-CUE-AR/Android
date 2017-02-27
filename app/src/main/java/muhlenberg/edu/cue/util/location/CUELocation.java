package muhlenberg.edu.cue.util.location;

import android.location.Location;

/**
 * Created by Jalal on 2/18/2017.
 * Added constructor for strings to points WPS 2/22/2017
 * Jalal is lame
 */

public class CUELocation {

    private double lat, lng, altitude;


    public CUELocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.altitude = location.getAltitude();
    }

    public CUELocation(double lat, double lng) {
        this(lat, lng, 0);
    }

    public CUELocation(double lat, double lng, double altitude) {
        this.lat = lat;
        this.lng = lng;
        this.altitude = altitude;
    }

    public CUELocation(String coordinates) {
        String[] coords = coordinates.split(",");
        this.lat = Float.parseFloat(coords[0]);
        this.lng = Float.parseFloat(coords[1]);
    }

    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return lng;
    }

    public void setLongitude(double lng) {
        this.lng = lng;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String toString() {
        return lat + "," + lng;
    }
}
