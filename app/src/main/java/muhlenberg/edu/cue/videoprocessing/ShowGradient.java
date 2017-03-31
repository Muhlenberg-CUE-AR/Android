package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import boofcv.abst.filter.derivative.ImageGradient;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.factory.filter.derivative.FactoryDerivative;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;

/**
 * Created by Jalal on 3/22/2017.
 */

public class ShowGradient extends VideoImageProcessing<GrayU8> {
    // Storage for the gradient
    private GrayS16 derivX = new GrayS16(1, 1);
    private GrayS16 derivY = new GrayS16(1, 1);

    // computes the image gradient
    private ImageGradient<GrayU8, GrayS16> gradient = FactoryDerivative.three(GrayU8.class, GrayS16.class);

    public ShowGradient() {
        super(ImageType.single(GrayU8.class));
        Log.d("cuear", "new showgradient created");
    }
    /**
     * Constructor
     *
     * @param imageType Type of image the video stream is to be converted to
     */
    public ShowGradient(ImageType<GrayU8> imageType) {
        super(ImageType.single(GrayU8.class));
    }

    @Override
    protected void declareImages(int width, int height) {
        super.declareImages(width, height);

        derivX.reshape(width, height);
        derivY.reshape(width, height);
    }

    @Override
    protected void process(GrayU8 image, Bitmap output, byte[] storage) {
        gradient.process(image, derivX, derivY);
        VisualizeImageData.colorizeGradient(derivX, derivY, -1, output, storage);
        Log.d("cuear", "processed in showgradient");
    }
}
