package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.LinearContourLabelChang2004;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import georegression.struct.point.Point2D_I32;

import static android.R.id.input;

/**
 * Created by Jalal on 3/31/2017.
 */

public class PolygonFitting extends VideoImageProcessing<GrayU8> {
    GrayU8 binary;
    GrayU8 filtered1;
    GrayS32 contourOutput;
    Paint paint = new Paint();
    RectF r = new RectF();
    LinearContourLabelChang2004 findContours = new LinearContourLabelChang2004(ConnectRule.EIGHT);

    public PolygonFitting() {
        super(ImageType.single(GrayU8.class));

    }

    @Override
    protected void declareImages(int width, int height) {
        super.declareImages(width, height);

        binary = new GrayU8(width,height);
        filtered1 = new GrayU8(width,height);
        contourOutput = new GrayS32(width,height);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.RED);

    }

    @Override
    protected void process(GrayU8 image, Bitmap output, byte[] storage) {
        // Select a reasonable threshold
        int mean = GThresholdImageOps.computeOtsu(image,0,255);

        // create a binary image by thresholding
        ThresholdImageOps.threshold(image, binary, mean, true);

        // reduce noise with some filtering
        BinaryImageOps.removePointNoise(binary, filtered1);

        // draw binary image for output
        VisualizeImageData.binaryToBitmap(filtered1, false, output, storage);

        // draw the ellipses
        findContours.process(filtered1,contourOutput);
        List<Contour> contours = findContours.getContours().toList();

        Canvas canvas = new Canvas(output);

        for( Contour contour : contours ) {
            List<Point2D_I32> points = contour.external;
            if( points.size() < 20 )
                continue;

            fitShape(points,canvas);
        }

    }

    protected void fitShape(List<Point2D_I32> contour, Canvas canvas) {
        List<PointIndex_I32> poly = ShapeFittingOps.fitPolygon(contour, true, 0.05, 0.025f, 10);

        for( int i = 1; i < poly.size(); i++ ) {
            PointIndex_I32 a = poly.get(i-1);
            PointIndex_I32 b = poly.get(i);

            canvas.drawLine(a.x,a.y,b.x,b.y,paint);
        }

        PointIndex_I32 a = poly.get(poly.size()-1);
        PointIndex_I32 b = poly.get(0);

        canvas.drawLine(a.x,a.y,b.x,b.y,paint);
    }
}

