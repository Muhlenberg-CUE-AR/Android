package muhlenberg.edu.cue;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.CameraView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Iterator;

import boofcv.android.gui.CameraPreview;
import boofcv.android.gui.VideoProcessing;
import muhlenberg.edu.cue.services.database.Building;
import muhlenberg.edu.cue.services.database.CUEDatabaseService;
import muhlenberg.edu.cue.services.location.CUELocationService;
import muhlenberg.edu.cue.util.fragments.CUEPopup;
import muhlenberg.edu.cue.videoprocessing.ShowGradient;

/**
 * Created by Jalal on 1/28/2017.
 * TODO: figure out why visualization wont render. moving to beyondarsurfaceview failed... try again?
 */
public class MainActivity extends AppCompatActivity implements LocationListener, OnTouchBeyondarViewListener, Camera.PreviewCallback {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;


    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

    private VideoProcessing processing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        CUEDatabaseService.getInstance().start(this);
        this.mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        this.world = new World(this);

        displayAllPOI();
    }

    @Override
    public void onResume() {
        super.onResume();
        CUELocationService.getInstance(this).start(this);
        CUEDatabaseService.getInstance().start(this);

        CameraView preview = mBeyondarFragment.getCameraView();
        preview.setVisibility(View.VISIBLE);
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        this.processing = new ShowGradient();
        this.processing.init(new Visualization(this), preview.getCamera(), info, preview.getCameraDisplayOrientation());
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
        Log.d("cuear", "received new location");
        if (loc == null)
            return;

        this.world.setGeoPosition(loc.getLatitude(), loc.getLongitude());
    }



    @Override
    public void onTouchBeyondarView(MotionEvent event, BeyondarGLSurfaceView beyondarView) {
        float x = event.getX();
        float y = event.getY();

        ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();

        beyondarView.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);
        Iterator<BeyondarObject> iterator = geoObjects.iterator();
        while(iterator.hasNext()) {
            BeyondarObject next = iterator.next();
            showPopup(next.getName());
        }
    }

    private void displayAllPOI() {
        Building[] buildings = CUEDatabaseService.getInstance().readAllPOI();
        for(int i=0; i<buildings.length; i++) {
            GeoObject poi = new GeoObject(buildings[i].getId());
            poi.setName(buildings[i].getName());
            poi.setText(buildings[i].getName());
            poi.setImageResource(R.drawable.base_poi_background);
            poi.setGeoPosition(buildings[i].getLat(), buildings[i].getLng());

            this.world.addBeyondarObject(poi);
        }

        this.world.setGeoPosition(40.550616, -75.402740);

        mBeyondarFragment.setWorld(this.world);
        mBeyondarFragment.setOnTouchBeyondarViewListener(this);
        mBeyondarFragment.startRenderingAR();
    }
    private void showPopup(String text) {
        DialogFragment newFragment = CUEPopup.newInstance();
        CUEPopup.text = text;
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(this.processing != null)
            processing.convertPreview(data, camera);
    }

    /**
     * Draws on top of the video stream for visualizing results from vision algorithms
     */
    private class Visualization extends SurfaceView {

        private Paint textPaint = new Paint();

        double history[] = new double[10];
        int historyNum = 0;

        Activity activity;

        long previous = 0;

        public Visualization(Activity context ) {
            super(context);
            this.activity = context;

            // Create out paint to use for drawing
            textPaint.setARGB(255, 200, 0, 0);
            textPaint.setTextSize(60);
            // This call is necessary, or else the
            // draw method will not be called.
            setWillNotDraw(false);
            setZOrderMediaOverlay(true);
        }

        @Override
        protected void onDraw(Canvas canvas){

            canvas.save();
            if( processing != null )
                processing.onDraw(canvas);
            Log.d("cuear", "Trying to render...");
            // Draw how fast it is running
            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;
            history[historyNum++] = 1000.0/elapsed;
            historyNum %= history.length;

            double meanFps = 0;
            for( int i = 0; i < history.length; i++ ) {
                meanFps += history[i];
            }
            meanFps /= history.length;

            // work around an issue in marshmallow
            try {
                canvas.restore();
            } catch( IllegalStateException e ) {
                if( !e.getMessage().contains("Underflow in restore - more restores than saves"))
                    throw e;
            }
        }
    }

}
