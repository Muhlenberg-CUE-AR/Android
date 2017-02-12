package muhlenberg.edu.cue.services;

import android.content.Context;
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

}
