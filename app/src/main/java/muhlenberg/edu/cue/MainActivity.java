package muhlenberg.edu.cue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Iterator;

import muhlenberg.edu.cue.services.database.Building;
import muhlenberg.edu.cue.services.database.CUEDatabaseService;
import muhlenberg.edu.cue.services.location.CUELocationService;
import muhlenberg.edu.cue.util.fragments.CUEPopup;

/**
 * Created by Jalal on 1/28/2017.
 */
public class MainActivity extends AppCompatActivity implements LocationListener, OnTouchBeyondarViewListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;


    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

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
        this.world = new World(this);
        displayAllPOI();
        

    }

    @Override
    public void onResume() {
        super.onResume();
        CUELocationService.getInstance(this).start(this);
        CUEDatabaseService.getInstance().start(this);

        CUELocationService.DEBUG = true;
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

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        this.world.setGeoPosition(40.550616, -75.402740);

        mBeyondarFragment.setWorld(this.world);
        mBeyondarFragment.startRenderingAR();
        mBeyondarFragment.setOnTouchBeyondarViewListener(this);
    }
    private void showPopup(String text) {
        DialogFragment newFragment = CUEPopup.newInstance();
        CUEPopup.text = text;
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
