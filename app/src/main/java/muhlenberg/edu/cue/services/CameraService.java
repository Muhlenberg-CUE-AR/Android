package muhlenberg.edu.cue.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.util.SizeF;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;

import java.lang.annotation.Target;

/**
 * Created by Willy on 2/12/2017.
 * Jalal is lame
 */

public class CameraService extends AbstractService {

    private Camera camera;
    private static CameraService instance;

    public static CameraService getInstance(Context context) {
        if (instance == null)
            instance = new CameraService(context);

        return instance;
    }

    private CameraService(Context context) {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("CameraFound", "Camera found");
                break;
            }
        }
        try {
            camera = camera.open(cameraId);
            /* Camera.Parameters params = camera.getParameters();
            double thetaV = Math.toRadians(params.getVerticalViewAngle());
            double thetaX = Math.toRadians(params.getHorizontalViewAngle());
            Log.d("Field of View",  "ThetaV: " + thetaV  ", ThetaX: " + thetaX);*/
        } catch(Exception e){
            Log.e("NoCameraAccess", "Unable to Access the Camera");
        }
    }

    public void start(Context context) {

    }

    public void stop(Context context) {
        // stops the camera capture
        camera.stopPreview();
        camera.release();
        camera = null;
    }

}
