package muhlenberg.edu.cue.util.geofence;

import android.location.Location;

/**
 * Created by Jalal on 2/16/2017.
 */
public class CUEGeoFence {

    Location[] corners;

    public CUEGeoFence() {
        this.corners = new Location[4];
    }

    public CUEGeoFence(Location[] corners) {
        this.corners = corners;
    }

    public CUEGeoFence(Location nw, Location ne, Location sw, Location se) {
        this();

        this.corners[0] = nw;
        this.corners[1] = ne;
        this.corners[2] = sw;
        this.corners[3] = se;
    }

    /**
     * Sort the points so that the order is [top left, top right, bottom left, bottom right]
     * with respect to the north pole. Lat/lng should be bounded by [-90, 90]
     *
     * Note that the north pole is (90, 0), and anything west of the prime meridian has a negative
     * longitude value, e.g. (40.598358, -75.510025) is Muhlenberg's lat/lng
     * Essentially like a Cartesian plane
     *
     * Therefore, the lat closest to 90 is most north, and most negative lng is most west
     * Although this method will sort the entire array regardless of size, a size of 4 is expected
     * @param corners
     */
    public static Location[] sort(Location[] corners) {

        if(corners == null || corners.length == 0
                || corners[0] == null
                || corners[1] == null
                || corners[2] == null
                || corners[3] == null) {
            throw new RuntimeException("Location[] is null or has null values or is empty");
        }

        //use bubble sort for a small, fixed array
        //sort by most northern
        for(int i=0;i<corners.length-1; i++) {
            for(int j=i; j<corners.length; j++) {
                if(corners[j].getLatitude() > corners[i].getLatitude()) {
                    Location temp = corners[i];
                    corners[i] = corners[j];
                    corners[j] = temp;
                }

                if(corners[j].getLongitude() < corners[i].getLongitude()) {
                    Location temp = corners[i];
                    corners[i] = corners[j];
                    corners[j] = temp;
                }
            }
        }

        return corners;
    }


    public Location[] getCorners() {
        return this.corners;
    }
}
