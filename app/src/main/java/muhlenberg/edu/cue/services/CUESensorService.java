package muhlenberg.edu.cue.services;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.gms.location.LocationServices;

import muhlenberg.edu.cue.MainActivity;

/**
 * Created by Will on 2/7/2017.
 */

public class CUESensorService extends AbstractService {

    private static CUESensorService instance;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor mMagnetometer;

    private CUESensorService(Context context){
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public static CUESensorService getInstance(Context context) {
        if (instance == null)
            instance = new CUESensorService(context);

        return instance;
    }

    @Override
    public void start(Context context) {
        mSensorManager.registerListener((MainActivity) context, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop(Context context) {
        mSensorManager.unregisterListener((MainActivity) context);
    }

    public void calculateFieldOfView(Context context) {
        MainActivity activity = (MainActivity) context;
        // get camera from camera preview
        Camera camera = activity.getCameraPreview().getCamera();
        if(camera != null) {
            Camera.Parameters params = camera.getParameters();
            // z is arbitrary, we assume they are about 50m from the object in view
            int z = 50;
            double thetaV = Math.toRadians(params.getVerticalViewAngle());
            double thetaH = Math.toRadians(params.getHorizontalViewAngle());
            // calculations for field of view
            double x = 2 * z * Math.tan(thetaH / 2);
            double y = 2 * z * Math.tan(thetaV / 2);
            Log.d("Field of View", "X: " + x + " Y: " + y);
        }
        else{
            Log.e("CameraNotOpen", "The Camera is not open");
            return;
        }
    }

}
