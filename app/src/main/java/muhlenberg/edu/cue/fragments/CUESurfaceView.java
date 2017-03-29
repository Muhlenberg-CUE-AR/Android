package muhlenberg.edu.cue.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Looper;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.beyondar.android.view.BeyondarGLSurfaceView;

import boofcv.android.gui.CameraPreview;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.android.gui.VideoProcessing;

/**
 * Created by Jalal on 3/22/2017.
 */

public class CUESurfaceView extends BeyondarGLSurfaceView {

    public CUESurfaceView(Context context) {
        super(context);
    }


}
