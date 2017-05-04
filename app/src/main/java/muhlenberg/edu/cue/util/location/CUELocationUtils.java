package muhlenberg.edu.cue.util.location;

import android.location.Location;

/**
 * Created by Jalal on 2/25/2017.
 */

public class CUELocationUtils {

    /**
     * Haversin formula for calculating distance between two points
     * @param camera
     * @param point
     * @return
     */
    public static double getDistance(CUELocation camera, CUELocation point) {
        //mean earth radius
        int earthRadius = 6371000;
        double diffLat = camera.getLatitudeRadians()-point.getLatitudeRadians();
        double diffLng = camera.getLongitudeRadians()-point.getLongitudeRadians();

        double a = Math.sin(diffLat/2) * Math.sin(diffLat/2) +
                Math.cos(camera.getLatitudeRadians()) * Math.cos(point.getLatitudeRadians()) *
                Math.sin(diffLng/2) * Math.sin(diffLng/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));

        return earthRadius*c;
    }

    /**
     * @see <a href="http://stackoverflow.com/a/5597308/7537946">Math is hard</a>
     * @param camera CUELocation of camera/user
     * @param point CUELocation of point of interest
     * @return
     */
    public static double getAngleAsDegrees(CUELocation camera, CUELocation point) {
        double diffLng = camera.getLongitudeRadians()-point.getLongitudeRadians();

        double y = Math.sin(diffLng) * Math.cos(point.getLatitudeRadians());
        double x = Math.cos(camera.getLatitudeRadians()) * Math.sin(point.getLatitudeRadians()) -
                Math.sin(camera.getLatitudeRadians()) *  Math.cos(point.getLatitudeRadians()) *
                Math.cos(diffLng);

        double angle = Math.atan2(y, x);
        double angleDeg = angle * 180.0/Math.PI;
        angle = ((angleDeg+360) % 360) * Math.PI/180.0;
        return angle * 180.0/Math.PI;

    }

    /**
     * @see <a href="http://web.archive.org/web/20121103042635/http://membres.multimania.fr/amycoders/tutorials/3dbasics.html">Math is really hard</a>
     *
     * @param angle angle between object and camera in radians
     * @param heading angle from north pole in radians
     * @param distance distance in meters between camera and object
     * @return
     */
    public static double getObjectScreenX(double angle, double heading, double distance) {
        double x = Math.sin(angle - heading) * distance;
        double z = Math.cos(angle - heading) * distance;

        return x * 256 / z;
    }

    public static double getObjectScreenY(double angle, double heading, double distance) {
        double y = Math.sin(angle - heading) * distance;
        double z = Math.cos(angle - heading) * distance;

        return y * 256 / z;
    }

    /*
    converts CUELocation object to Location object
     */
    public static Location cueToAndroidLocation(CUELocation myLocation) {
        Location loc = new Location("");
        if(myLocation != null) {
            loc.setLatitude(myLocation.getLatitude());
            loc.setLongitude(myLocation.getLongitude());
            loc.setAltitude(myLocation.getAltitude());
        }

        return loc;
    }
}
