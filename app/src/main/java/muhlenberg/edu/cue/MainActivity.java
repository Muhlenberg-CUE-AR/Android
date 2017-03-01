package muhlenberg.edu.cue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.rendering.ARRenderer;

import muhlenberg.edu.cue.services.CUELocationService;
import muhlenberg.edu.cue.services.CUESensorService;
import muhlenberg.edu.cue.util.geofence.CUEGeoFence;
import muhlenberg.edu.cue.util.location.CUELocation;
import muhlenberg.edu.cue.util.location.CUELocationUtils;
import muhlenberg.edu.cue.util.renderer.CUERenderer;

/**
 * Created by Jalal on 1/28/2017.
 */
public class MainActivity extends ARActivity implements LocationListener, SensorEventListener, CameraEventListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;
    /**
     * A custom renderer to manage custom object rendering
     */
    private CUERenderer cueRenderer;

    private CUELocationService locationService;
    private CUESensorService sensorService;

    private CUEGeoFence eastFence;
    private CUEGeoFence moyerFence;
    private CUEGeoFence ettingerFence;
    private CUELocation eastCourtyard;
    private CUELocation lastKnownPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        this.cueRenderer = new CUERenderer(this);
        this.locationService = CUELocationService.getInstance(this);
        this.sensorService = CUESensorService.getInstance(this);

        CUELocation[] east = {new CUELocation(40.598865, -75.508924),
                new CUELocation(40.599081, -75.508162),
                new CUELocation(40.598283, -75.508586)};

        CUELocation[] moyer = {new CUELocation(40.598091, -75.509107),
                new CUELocation(40.598297, -75.508383),
                new CUELocation(40.597533, -75.508823)};

        CUELocation[] ettinger = {new CUELocation(40.597935, -75.509952),
                new CUELocation(40.598092, -75.509112),
                new CUELocation(40.597538, -75.509732)};


        eastFence = new CUEGeoFence(east[0], east[1], east[2]);
        moyerFence = new CUEGeoFence(moyer[0], moyer[1], moyer[2]);
        ettingerFence = new CUEGeoFence(ettinger[0], ettinger[1], ettinger[2]);

        this.eastCourtyard = new CUELocation(40.598932, -75.508470);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.locationService.start(this);
        this.sensorService.start(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.locationService.stop(this);
        this.sensorService.stop(this);
    }

    /**
     * Provide our own SimpleRenderer.
     */
    @Override
    protected ARRenderer supplyRenderer() {
        if (!checkCameraPermission()) {
            Toast.makeText(this, "No camera permission - restart the app", Toast.LENGTH_LONG).show();
            return null;
        }

        return cueRenderer;
    }

    /**
     * Use the FrameLayout in this Activity's UI.
     */
    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.mainLayout);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc == null)
            return;

        this.lastKnownPosition = new CUELocation(loc);

    }

    private float[] accel = null;
    private float[] geomagentic = null;

    // when any sensor gets a new value this function is run
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (lastKnownPosition == null || accel == null || geomagentic == null)
            return;

        if (CUEGeoFence.shouldActivate(lastKnownPosition)) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accel = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomagentic = event.values;

            if (accel != null && geomagentic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accel, geomagentic);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimuthRadians = orientation[0];

                    double distance = CUELocationUtils.getDistance(lastKnownPosition, eastCourtyard);
                    double angle = CUELocationUtils.getAngleAsDegrees(lastKnownPosition, eastCourtyard) * Math.PI / 180.0;
                    double x = CUELocationUtils.getObjectScreenX(angle, azimuthRadians, distance);
                    double y = CUELocationUtils.getObjectScreenY(angle, azimuthRadians, distance);

                    int sx = (int) Math.floor(x);
                    int sy = (int) Math.floor(y);
                    cueRenderer.setText("Welcome to East!", sx, sy);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
