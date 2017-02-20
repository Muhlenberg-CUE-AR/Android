package muhlenberg.edu.cue.util.geofence;

import android.util.Log;

import muhlenberg.edu.cue.util.location.CUELocation;

/**
 * Created by Jalal on 2/16/2017.
 */
public class CUEGeoFence {

    private CUELocation[] corners;
    public static final double VALIDATION_TOLERANCE = 0.001;

    public CUEGeoFence() {
        this.corners = new CUELocation[4];
    }

    public CUEGeoFence(CUELocation[] corners) {
        this.corners = corners;
        validate(corners, VALIDATION_TOLERANCE);
    }

    public CUEGeoFence(CUELocation nw, CUELocation ne, CUELocation sw, CUELocation se) {
        this();

        this.corners[0] = nw;
        this.corners[1] = ne;
        this.corners[2] = sw;
        this.corners[3] = se;
        validate(this.corners, VALIDATION_TOLERANCE);
    }

    /**
     * {@link http://stackoverflow.com/a/2304031/7537946}
     * Checks if the corners make a valid rectangle, ie each side is an approximately consistent length
     * This is done by finding the center of mass of the rectangle and measuring the distance to each
     * corner. It should be equidistant from each corner, within a floating point margin of error.
     *
     * This method is called from the constructor, but is also provided publicly for convenience
     * @param corners array of corners representing the bouding box
     * @param tolerance the magnitude of acceptable difference between side lengths. should be fairly
     *                  small
     * @return true if the corners form a rectangle, false otherwise
     */
    public boolean validate(CUELocation[] corners, double tolerance) {
        //check if corners are valid and operable
        if(corners.length != 4 || corners[0] == null || corners[1] == null
                || corners[2] == null || corners[3] == null) {
            Log.d("cuear", "Corners are null in validate() of CUEGeoFence! Has the object been initialized yet?");
            return false;
        }

        double cx = 0.0, cy = 0.0;
        for(CUELocation corner : corners){
            cx += corner.getLatitude();
            cy += corner.getLongitude();
        }
        cx /= corners.length;
        cy /= corners.length;

        double sqD1 = Math.pow(cx-corners[0].getLatitude(), 2) - Math.pow(cy-corners[0].getLongitude(), 2);
        double sqD2 = Math.pow(cx-corners[1].getLatitude(), 2) - Math.pow(cy-corners[1].getLongitude(), 2);
        double sqD3 = Math.pow(cx-corners[2].getLatitude(), 2) - Math.pow(cy-corners[2].getLongitude(), 2);
        double sqD4 = Math.pow(cx-corners[3].getLatitude(), 2) - Math.pow(cy-corners[3].getLongitude(), 2);

        return Math.abs(sqD1 - sqD2) <= tolerance
                && Math.abs(sqD1 - sqD3) <= tolerance
                && Math.abs(sqD1 - sqD4) <= tolerance;

    }

    /**
     * {@link http://math.stackexchange.com/a/190373}
     * Tests if a point is inside the bounding box (rectangle) formed by the corners.
     * @param corners
     * @param point
     * @return
     */
    public boolean isInsideBoundingBox(CUELocation[] corners, CUELocation point) {
        if(corners.length != 4 || corners[0] == null || corners[1] == null
                || corners[2] == null || corners[3] == null || point == null) {
            Log.d("cuear", "Null point or corners passed to isInsideBoundingBox() of CUEGeoFence! Is locationlistener ready to receive input?");
            return false;
        }
        double[] a = {this.corners[0].getLatitude(), this.corners[0].getLongitude()};
        double[] b = {this.corners[1].getLatitude(), this.corners[1].getLongitude()};
        double[] c = {this.corners[2].getLatitude(), this.corners[2].getLongitude()};

        double[] m = {point.getLatitude(), point.getLongitude()};

        //0 <= dot(AB,AM) <= dot(AB,AB) &&  0 <= dot(BC,BM) <= dot(BC,BC)
        //recall that vector AB = (b.x - a.x, b.y - a.y)
        //and AB dot AM = AB.x * AM.x + AB.y * AM.y
        //which expands to AB dot AM = (b.x - a.x) * (m.x - a.x) + (b.y - a.y) * (m.y - a.y)
        double abam = (b[0] - a[0]) * (m[0] - a[0]) + (b[1] - a[1]) * (m[1] - a[1]);
        double abab = (b[0] - a[0]) * (b[0] - a[0]) + (b[1] - a[1]) * (b[1] - a[1]);
        double bcbm = (c[0] - b[0]) * (m[0] - b[0]) + (c[1] - b[1]) * (m[1] - b[1]);
        double bcbc = (c[0] - b[0]) * (c[0] - b[0]) + (b[1] - c[1]) * (b[1] - c[1]);

        if( 0 <= abam && abam <= abab && 0 <= bcbm && bcbm <= bcbc)
            return true;

        return false;
    }

    public CUELocation[] getCorners() {
        return this.corners;
    }

}
