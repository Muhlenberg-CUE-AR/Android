package muhlenberg.edu.cue.services.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import muhlenberg.edu.cue.MainActivity;
import muhlenberg.edu.cue.services.AbstractService;

/**
 * Created by Will on 2/7/2017.
 */

public class CUESensorService extends AbstractService {

    private static CUESensorService instance;
    private SensorManager mSensorManager;
    Sensor mMagnetometer;
    Sensor mAccelerometer;

    public static CUESensorService getInstance() {
        if (instance == null)
            instance = new CUESensorService();

        return instance;
    }

    @Override
    public void start(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener((MainActivity) context, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener((MainActivity) context, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void stop(Context context) {
        mSensorManager.unregisterListener((MainActivity) context);
    }

}
