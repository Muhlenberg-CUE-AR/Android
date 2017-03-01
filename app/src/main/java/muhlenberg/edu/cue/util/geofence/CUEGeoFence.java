package muhlenberg.edu.cue.util.geofence;

import android.util.Log;

import java.util.Arrays;

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

    /**
     * Creates a geofence out of an array of CUELocations. The first three locations are used to
     * calculate the fourth so accuracy is maintained. It is recommended the locations are listed in
     * order of Northwest, Northeast, and Southwest. The last element will be overwritten if it contains
     * an object.
     * @param corners Array of length 4 of CUELocation objects.
     */
    public CUEGeoFence(CUELocation[] corners) throws Exception {
        this.corners = corners;
        if(corners.length != 4) {
            throw new Exception("Array needs to be of length 4!");
        }
        if(!validate(corners[0], corners[1], corners[2])) {
            Log.d("cuear", "Corners do not form a right angle! Calculations may be off in the future.");
        }

        if(corners == null || corners[0] == null || corners[1] == null || corners[2] == null) {
            Log.d("cuear", "passed a null corner array or it  had null values. Please fix to continue");
            throw new RuntimeException();
        }
        this.corners[3] = calcLastCorner(this.corners[0], this.corners[1], this.corners[2]);

    }

    /**
     * Create a geofence using 3 coordinates, preferably the NorthWest, NorthEast, and SouthWest
     * coordinates. Most calculations will operate fine without being in order, but some may fail
     * or be inaccurate. The fourth point of the geofence is calculated from the first three to
     * maintain accuracy.
     * @param nw Northwest point
     * @param ne Northeast point
     * @param sw Southwest point
     */
    public CUEGeoFence(CUELocation nw, CUELocation ne, CUELocation sw) {
        this();

        this.corners[0] = nw;
        this.corners[1] = ne;
        this.corners[2] = sw;

        if(!validate(nw, ne, sw)) {
            Log.d("cuear", "Corners do not form a right angle! Calculations may be off in the future.");
        }

        this.corners[3] = calcLastCorner(nw, ne, sw);
    }


    /**
     * @see <a href="http://stackoverflow.com/a/21659153/7537946">Amazing</a>
     * Find the last point of a rectangle given 3 points. This allows us to not have
     * perfect precision when measuring lat/lng values in the real world and provides
     * more accuracy in bounds calcualtions.
     * Although the algorithm does not require the points to be in order, it is
     * recommended they are provided correctly to maintain readability
     * @param nw Northwest point
     * @param ne Northeast point
     * @param sw Southwest point
     * @return se Southeast point
     */
    public static CUELocation calcLastCorner(CUELocation nw, CUELocation ne, CUELocation sw) {
        double x = Double.longBitsToDouble(Double.doubleToLongBits(nw.getLatitude())
                ^ Double.doubleToLongBits(ne.getLatitude())
                ^ Double.doubleToLongBits(sw.getLatitude()));

        double y = Double.longBitsToDouble(Double.doubleToLongBits(nw.getLongitude())
                ^ Double.doubleToLongBits(ne.getLongitude())
                ^ Double.doubleToLongBits(sw.getLongitude()));

        return new CUELocation(x, y);
    }

    /**
     * Use the Distance formula to determine if the 3 provided points form a right angle
     * Although the algorithm does not require the points to be in order, it is
     * recommended they are provided correctly to maintain readability
     * @param nw Northwest point
     * @param ne Northeast point
     * @param sw Southwest point
     * @return true if the corners form a right angle, false otherwise
     */
    public static  boolean validate(CUELocation nw, CUELocation ne, CUELocation sw) {
        //check if corners are valid and operable
        if (nw == null || ne == null || sw == null) {
            Log.d("cuear", "Corners are null in validate() of CUEGeoFence! Have the objects been initialized yet?");
            return false;
        }

        //square the 2 smaller distance and sum them.
        //if they equal the square of the larger distance, the points form a right angle

        double x = Math.sqrt(Math.pow(ne.getLatitude() - nw.getLatitude(), 2) + Math.pow(ne.getLongitude() - nw.getLongitude(), 2));
        double y = Math.sqrt(Math.pow(nw.getLatitude() - sw.getLatitude(), 2) + Math.pow(nw.getLongitude() - sw.getLongitude(), 2));
        double z = Math.sqrt(Math.pow(ne.getLatitude() - sw.getLatitude(), 2) + Math.pow(ne.getLongitude() - sw.getLongitude(), 2));

        double[] dists = {x, y, z};
        Arrays.sort(dists);

        return Math.abs(dists[0] * dists[0] + dists[1] * dists[1] - dists[2] * dists[2]) < 0.001;
    }

    /**
     * @see <a href="http://math.stackexchange.com/a/190373">Source</a>
     * Tests if a point is inside the bounding box (rectangle) formed by the corners.
     *
     * @param corners
     * @param point
     * @return
        */
        public static boolean isInsideBoundingBox(CUELocation[] corners, CUELocation point) {
            if (corners.length != 4 || corners[0] == null || corners[1] == null
                    || corners[2] == null || corners[3] == null || point == null) {
                Log.d("cuear", "Null point or corners passed to isInsideBoundingBox() of CUEGeoFence! Is locationlistener ready to receive input?");
                return false;
            }
        double[] a = {corners[0].getLatitude(), corners[0].getLongitude()};
        double[] b = {corners[1].getLatitude(), corners[1].getLongitude()};
        double[] c = {corners[2].getLatitude(), corners[2].getLongitude()};

        double[] m = {point.getLatitude(), point.getLongitude()};

        //0 <= dot(AB,AM) <= dot(AB,AB) &&  0 <= dot(BC,BM) <= dot(BC,BC)
        //recall that vector AB = (b.x - a.x, b.y - a.y)
        //and AB dot AM = AB.x * AM.x + AB.y * AM.y
        //which expands to AB dot AM = (b.x - a.x) * (m.x - a.x) + (b.y - a.y) * (m.y - a.y)
        double abam = (b[0] - a[0]) * (m[0] - a[0]) + (b[1] - a[1]) * (m[1] - a[1]);
        double abab = (b[0] - a[0]) * (b[0] - a[0]) + (b[1] - a[1]) * (b[1] - a[1]);
        double bcbm = (c[0] - b[0]) * (m[0] - b[0]) + (c[1] - b[1]) * (m[1] - b[1]);
        double bcbc = (c[0] - b[0]) * (c[0] - b[0]) + (b[1] - c[1]) * (b[1] - c[1]);

        if (0 <= abam && abam <= abab && 0 <= bcbm && bcbm <= bcbc)
            return true;

        return false;
    }

    /**
     * TODO: come up with a fast way to calculate if we are inside any bounding box
     * @param location
     * @return
     */
    public static boolean shouldActivate(CUELocation location) {

        return true;
    }

    public CUELocation[] getCorners() {
        return this.corners;
    }

}
