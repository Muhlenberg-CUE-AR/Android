package muhlenberg.edu.cue;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragment;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.abst.feature.detect.line.DetectLineHoughFootSubimage;
import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.factory.feature.detect.line.ConfigHoughFoot;
import boofcv.factory.feature.detect.line.ConfigHoughFootSubimage;
import boofcv.factory.feature.detect.line.ConfigHoughPolar;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import muhlenberg.edu.cue.services.database.Building;
import muhlenberg.edu.cue.services.database.CUEDatabaseService;
import muhlenberg.edu.cue.services.database.Tour;
import muhlenberg.edu.cue.services.location.CUELocationService;
import muhlenberg.edu.cue.util.fragments.CUEPopup;
import muhlenberg.edu.cue.util.location.CUELocation;
import muhlenberg.edu.cue.util.location.CUELocationUtils;
import muhlenberg.edu.cue.videoprocessing.LineDetector;


/**
 * Created by Jalal on 1/28/2017.
 * Willy made some changes to this throughout the project
 */
public class MainActivity extends VideoDisplayActivity implements LocationListener, OnTouchBeyondarViewListener, SensorEventListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;


    private BeyondarFragment beyondarFragment;
    private World world;
    private Tour tour;
    // used for compass heading
    private ImageView image;
    private SensorManager mSensorManager;
    private GeomagneticField geoField;
    private float myBearing = 0f;
    private float currentDegree = 0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        CUEDatabaseService.getInstance().start(this);

        getViewContent().removeAllViews();
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setId(100);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout preview = getViewPreview();

        mainLayout.addView(preview);

        setContentView(mainLayout, params);

        image = new ImageView(this);
        image.setImageResource(R.drawable.pointer);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(100, 100);
        imageParams.setMargins(100, 100, 100, 100);
        image.setLayoutParams(imageParams);
        preview.addView(image, imageParams);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        beyondarFragment = new BeyondarFragment();
        getFragmentManager().beginTransaction().add(mainLayout.getId(), beyondarFragment, "beyondar").commit();
        getFragmentManager().executePendingTransactions();

        setShowFPS(false);

    }

    @Override
    public void onResume() {
        CUELocationService.getInstance(this).start(this);
        CUEDatabaseService.getInstance().start(this);

        // gets all the locations on a certain tour route
        this.tour = CUEDatabaseService.getInstance().readTour();
        this.tour.setPointList(CUEDatabaseService.getInstance().readPointList());
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);


        DetectLine<GrayU8> detector = FactoryDetectLineAlgs.houghFoot(
                new ConfigHoughFoot(5, 5, 5, 30, 4), GrayU8.class, GrayS16.class);
        DetectLineHoughFootSubimage detectLineSegment = FactoryDetectLineAlgs.houghFootSub(
                new ConfigHoughFootSubimage(5, 5, 5, 30, 2, 2, 2), GrayU8.class, GrayS16.class);

        setProcessing(new LineDetector(detectLineSegment));

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected Camera openConfigureCamera(Camera.CameraInfo cameraInfo) {
        Camera mCamera = selectAndOpenCamera(cameraInfo);
        Camera.Parameters param = mCamera.getParameters();

        // Select the preview size closest to 320x240
        // Smaller images are recommended because some computer vision operations are very expensive
        List<Camera.Size> sizes = param.getSupportedPreviewSizes();
        Camera.Size s = sizes.get(closest(sizes, 320, 240));
        param.setPreviewSize(s.width, s.height);
        mCamera.setParameters(param);

        displayAllPOI();

        return mCamera;
    }

    /**
     * Step through the camera list and select a camera.  It is also possible that there is no camera.
     * The camera hardware requirement in AndroidManifest.xml was turned off so that devices with just
     * a front facing camera can be found.  Newer SDK's handle this in a more sane way, but with older devices
     * you need this work around.
     */
    private Camera selectAndOpenCamera(Camera.CameraInfo info) {
        int numberOfCameras = Camera.getNumberOfCameras();

        int selected = -1;

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                selected = i;
                break;
            } else {
                // default to a front facing camera if a back facing one can't be found
                selected = i;
            }
        }

        if (selected == -1) {
            dialogNoCamera();
            return null; // won't ever be called
        } else {
            return Camera.open(selected);
        }
    }

    /**
     * Gracefully handle the situation where a camera could not be found
     */
    private void dialogNoCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device has no cameras!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Goes through the size list and selects the one which is the closest specified size
     */
    public static int closest(List<Camera.Size> sizes, int width, int height) {
        int best = -1;
        int bestScore = Integer.MAX_VALUE;

        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size s = sizes.get(i);

            int dx = s.width - width;
            int dy = s.height - height;

            int score = dx * dx + dy * dy;
            if (score < bestScore) {
                best = i;
                bestScore = score;
            }
        }

        return best;
    }

    @Override
    public void onStop() {
        super.onStop();
        CUELocationService.getInstance(this).stop(this);
        CUEDatabaseService.getInstance().stop(this);
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

        this.world.setGeoPosition(loc.getLatitude(), loc.getLongitude());

        // checks to see if the user is on the path
        CUELocation myLocation = new CUELocation(this.world.getLatitude(), this.world.getLongitude());
        CUELocationService.isLocationOnPath(myLocation, this.tour.getPointList(), 10);

        Location currLocation = CUELocationUtils.cueToAndroidLocation(myLocation);
        Location destLocation = CUELocationUtils.cueToAndroidLocation(this.tour.getPointList().get(0));
        geoField = new GeomagneticField(
                Double.valueOf(loc.getLatitude()).floatValue(),
                Double.valueOf(loc.getLongitude()).floatValue(),
                Double.valueOf(loc.getAltitude()).floatValue(),
                System.currentTimeMillis() );
        myBearing = currLocation.bearingTo(destLocation);
        myBearing = myBearing * -1;
    }


    @Override
    public void onTouchBeyondarView(MotionEvent event, BeyondarGLSurfaceView beyondarView) {
        HashMap<Long, Building> buildings = CUEDatabaseService.getInstance().readAllPOIasMap();
        float x = event.getX();
        float y = event.getY();

        ArrayList<BeyondarObject> geoObjects = new ArrayList<>();
        beyondarView.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);

        Iterator<BeyondarObject> it = geoObjects.iterator();
        while(it.hasNext()) {
            Building next = buildings.get(it.next().getId());
            showPopup(next.getName(), next.getLongDesc());
        }

    }

    private void displayAllPOI() {
        this.world = new World(this);
        this.world.setDefaultImage(R.drawable.road_overlay);
        Building[] buildings = CUEDatabaseService.getInstance().readAllPOI();

        for (int i = 0; i < buildings.length; i++) {
            GeoObject poi = new GeoObject(buildings[i].getId());
            poi.setName(buildings[i].getName());
            poi.setText(buildings[i].getName(), buildings[i].getShortDesc());
            poi.setDBID(buildings[i].getId());
            int resource = getResources().getIdentifier("base_poi_background" + i, "drawable", getPackageName());
            poi.setImageResource(resource);
            poi.setGeoPosition(buildings[i].getLat(), buildings[i].getLng());

            this.world.addBeyondarObject(poi);
        }

        this.world.setGeoPosition(40.550616, -75.402740);
        beyondarFragment.setMaxDistanceToRender(1609); //one mile
        beyondarFragment.setWorld(this.world);
        beyondarFragment.setOnTouchBeyondarViewListener(this);
        beyondarFragment.setDistanceFactor(10);
        beyondarFragment.setPushAwayDistance(10);
    }

    private void showPopup(String name, String longDesc) {
        DialogFragment newFragment = CUEPopup.newInstance();
        CUEPopup.text =  name + "\n\n" + longDesc;
        newFragment.show(getFragmentManager(), "dialog");

    }

    public void onSensorChanged(SensorEvent event) {
        float azimuth = event.values[0];
        try {
            azimuth += geoField.getDeclination();
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        azimuth = (myBearing - azimuth) * -1;

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        ra.setDuration(210);
        ra.setFillAfter(true);

        image.startAnimation(ra);
        currentDegree = -azimuth;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // we are not using this as of now
    }

}
