package muhlenberg.edu.cue.videoprocessing;

/**
 * Created by Jalal on 3/29/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import boofcv.android.gui.VideoProcessing;

/**
 * Draws on top of the video stream for visualizing results from vision algorithms
 */
public class Visualization extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Paint textPaint = new Paint();

    private VideoProcessing processing;

    public Visualization(Context context) {
        super(context);
        init(context);

    }

    public Visualization(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public Visualization(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Visualization(Context context, VideoProcessing processing) {
        super(context);

        this.processing = processing;
        init(context);

    }

    private void init(Context context) {
        getHolder().addCallback(this);
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setTextSize(60);
        setWillNotDraw(false);
        setZOrderOnTop(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        if (processing != null) {
            processing.onDraw(canvas);
        }

        // work around an issue in marshmallow
        try {
            canvas.restore();
        } catch (IllegalStateException e) {
            if (!e.getMessage().contains("Underflow in restore - more restores than saves"))
                throw e;
        }


    }

    /**
     * Changes the CV algorithm running.  Should only be called from a GUI thread.
     */
    public void setProcessing( VideoProcessing processing ) {
        if( this.processing != null ) {
            // kill the old process
            this.processing.stopProcessing();
        }

        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("Not called from a GUI thread. Bad stuff could happen");
        }

        this.processing = processing;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("cuear", "visualization created");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("cuear", "visualization rotated??");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("cuear", "visualization destroyed!");
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.processing != null) {
            processing.convertPreview(data, camera);
            Log.d("cuear", "converting preview frame");
        }

    }
}