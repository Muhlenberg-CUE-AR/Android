package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.ddogleg.struct.FastQueue;

import boofcv.abst.filter.binary.InputToBinary;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.shapes.polygon.BinaryPolygonDetector;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.factory.shape.ConfigPolygonDetector;
import boofcv.factory.shape.FactoryShapeDetector;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;

/**
 * Created by Jalal on 3/31/2017.
 */

public class PolygonFitting extends VideoImageProcessing<GrayU8> {
    Paint paint;
    int minSides = 3;
    int maxSides = 5;

    static final int MAX_SIDES = 20;
    static final int MIN_SIDES = 3;

    boolean sidesUpdated = false;

    BinaryPolygonDetector<GrayU8> detector;
    InputToBinary<GrayU8> inputToBinary;

    GrayU8 binary = new GrayU8(1,1);

    int colors[] = new int[MAX_SIDES - MIN_SIDES + 1];


    public PolygonFitting() {
        super(ImageType.single(GrayU8.class));
        double rgb[] = new double[3];

        for (int i = 0; i < colors.length; i++) {
            double frac = i/(double)(colors.length);

            double hue = 2*Math.PI*frac;
            double sat = 1.0;

            ColorHsv.hsvToRgb(hue,sat,255,rgb);

            colors[i] = 255 << 24 | ((int)rgb[0] << 16) | ((int)rgb[1] << 8) | (int)rgb[2];
        }

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3.0f);

        ConfigPolygonDetector configPoly = new ConfigPolygonDetector(3,5);
        configPoly.convex = false;

        detector = FactoryShapeDetector.polygon(configPoly,GrayU8.class);
    }

    @Override
    protected void declareImages(int width, int height) {
        super.declareImages(width, height);
        binary.reshape(width,height);
    }

    @Override
    protected void process(GrayU8 image, Bitmap output, byte[] storage) {
        if( sidesUpdated ) {
            sidesUpdated = false;
            detector.setNumberOfSides(minSides,maxSides);
        }

        synchronized ( this ) {
            inputToBinary.process(image,binary);
        }

        detector.process(image,binary);

//        if( showInput ) {
//            ConvertBitmap.boofToBitmap(image,output,storage);
//        } else {
            VisualizeImageData.binaryToBitmap(binary,false,output,storage);
//        }

        Canvas canvas = new Canvas(output);

        FastQueue<Polygon2D_F64> found = detector.getFoundPolygons();

        for( Polygon2D_F64 s : found.toList() )  {
            paint.setColor(colors[s.size()-MIN_SIDES]);

            for (int i = 1; i < s.size(); i++) {
                Point2D_F64 a = s.get(i-1);
                Point2D_F64 b = s.get(i);
                canvas.drawLine((float)a.x,(float)a.y,(float)b.x,(float)b.y,paint);
            }
            Point2D_F64 a = s.get(s.size()-1);
            Point2D_F64 b = s.get(0);
            canvas.drawLine((float)a.x,(float)a.y,(float)b.x,(float)b.y,paint);
        }
    }

}

