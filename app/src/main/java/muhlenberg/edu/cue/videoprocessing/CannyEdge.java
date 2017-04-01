package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;

/**
 * Created by Jalal on 3/29/2017.
 */

public class CannyEdge extends VideoImageProcessing<GrayU8> {

    boofcv.alg.feature.detect.edge.CannyEdge<GrayU8, GrayS16> canny;

    private final float threshold = 0.4f;
    public CannyEdge() {
        super(ImageType.single(GrayU8.class));
        this.canny = FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);
    }



    @Override
    protected void process(GrayU8 image, Bitmap output, byte[] storage) {
        canny.process(image,threshold/3.0f,1.0f,null);
        List<EdgeContour> contours = canny.getContours();
        VisualizeImageData.drawEdgeContours(contours,0xFF0000,output,storage);
    }
}
