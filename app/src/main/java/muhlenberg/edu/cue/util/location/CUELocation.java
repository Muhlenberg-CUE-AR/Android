package muhlenberg.edu.cue.util.location;

import android.location.Location;

/**
 * Created by Jalal on 2/18/2017.
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
}
