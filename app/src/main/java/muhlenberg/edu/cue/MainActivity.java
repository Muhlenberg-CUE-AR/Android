package muhlenberg.edu.cue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
import muhlenberg.edu.cue.util.text.CUERenderer;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

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
    public void onLocationChanged(Location location) {
        Log.d("cuear", "received new location");
        if (location != null && CUEGeoFence.isInsideBoundingBox(eastFence.getCorners(), new CUELocation(location)))
            cueRenderer.setText("Welcome to East!");
        else if (location != null && CUEGeoFence.isInsideBoundingBox(moyerFence.getCorners(), new CUELocation(location)))
            cueRenderer.setText("Welcome to Moyer!");
        else if (location != null && CUEGeoFence.isInsideBoundingBox(ettingerFence.getCorners(), new CUELocation(location)))
            cueRenderer.setText("Welcome to Ettinger!");
        else {
            Log.d("cuear", "you done fucked up");
        }
        // check if camera is open

        // calculate field of view
        sensorService.calculateFieldOfView(this);
    }

    // when any sensor gets a new value this function is run
    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuthRadians = event.values[0];
        Float azimuthDegrees = (azimuthRadians * 180) / (float) Math.PI;
        Log.d("Azimuth", azimuthDegrees.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Will", "Will is cooler than Jalal");
    }

}
