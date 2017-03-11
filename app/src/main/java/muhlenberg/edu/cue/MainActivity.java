package muhlenberg.edu.cue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.opengl.renderable.SquareRenderable;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Iterator;

import muhlenberg.edu.cue.services.location.CUELocationService;

/**
 * Created by Jalal on 1/28/2017.
 */
public class MainActivity extends AppCompatActivity implements LocationListener, OnTouchBeyondarViewListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 133;


    private BeyondarFragmentSupport mBeyondarFragment;
    private World world;

    private Point3 roadEnd = new Point3(40.550939f, -75.401617f, 0.0f);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        this.world = new World(this);
        this.world.setGeoPosition(40.550616, -75.402740);

        GeoObject frontDoor = new GeoObject(1l);
        frontDoor.setImageResource(R.drawable.home_text);
        frontDoor.setGeoPosition(40.550699, -75.402833);
        frontDoor.setName("Home");


        GeoObject road = new GeoObject(2l);
        road.setGeoPosition(40.550781f, -75.403289f);
        road.setImageResource(R.drawable.road_overlay);
        road.setName("Road");

        this.world.addBeyondarObject(frontDoor);
        this.world.addBeyondarObject(road);

        mBeyondarFragment.setWorld(this.world);
        mBeyondarFragment.setOnTouchBeyondarViewListener(this);
        mBeyondarFragment.setMaxDistanceToRender(500);
    }

    @Override
    public void onResume() {
        super.onResume();
        CUELocationService.getInstance(this).start(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CUELocationService.getInstance(this).stop(this);
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
            DialogFragment newFragment = MyDialogFragment.newInstance();
            MyDialogFragment.text = next.getName();
            newFragment.show(getSupportFragmentManager(), "dialog");
        }
    }

    public static class MyDialogFragment extends DialogFragment {

        public static String text;
        static MyDialogFragment newInstance() {
            text = "Hello World";
            return new MyDialogFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.popup, container, false);
            View tv = v.findViewById(R.id.popupText);
            ((TextView)tv).setText(text);
            return v;
        }
    }
}
