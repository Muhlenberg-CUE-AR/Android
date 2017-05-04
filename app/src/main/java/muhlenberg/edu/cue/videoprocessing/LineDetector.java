package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import org.ddogleg.struct.FastQueue;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.line.LineImageOps;
import boofcv.alg.filter.basic.GrayImageOps;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoRenderProcessing;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import georegression.metric.Intersection2D_F32;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F32;

/**
 * Created by Jalal on 4/1/2017.
 */

public class LineDetector extends VideoRenderProcessing<GrayU8> {

    private DetectLine<GrayU8> detector;
    private FastQueue<LineSegment2D_F32> lines = new FastQueue<>(LineSegment2D_F32.class, true);

    private Bitmap bitmap;
    private byte[] storage;
    private static Bitmap cam;
    private Paint paint = new Paint();
    private Paint roadPaint = new Paint();

    public final static boolean DEBUG = false;
    private final double ANGLE_TOLERANCE = 15; //in degrees
    private final double DESIRED_ANGLE = 25;   //in degrees
    private CannyEdge<GrayU8, GrayS16> canny;
    private Point2D_F32 intersection;

    public LineDetector(DetectLine<GrayU8> detector) {
        super(ImageType.single(GrayU8.class));
        this.detector = detector;

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        roadPaint.setColor(Color.argb(60,0,0,200));
        roadPaint.setStyle(Paint.Style.FILL);

        this.canny = FactoryEdgeDetectors.canny(5, true, true, GrayU8.class, GrayS16.class);
    }

    @Override
    protected void declareImages(int width, int height) {
        super.declareImages(width, height);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        storage = ConvertBitmap.declareStorage(bitmap, storage);

    }


    @Override
    protected void process(GrayU8 gray) {
        if (detector != null) {
            GrayImageOps.brighten(gray, -30, 200, gray);

            canny.process(gray, 0.1f, 0.3f, gray);
            VisualizeImageData.drawEdgeContours(canny.getContours(), 0xFF0000, bitmap, storage);
            ConvertBitmap.bitmapToGray(bitmap, gray, null);
            List<LineParametric2D_F32> found = detector.detect(gray);

//            filterNearlyDesiredAngles(found, DESIRED_ANGLE, ANGLE_TOLERANCE);

            float t = Intersection2D_F32.intersection(found.get(0), found.get(1));
            intersection = found.get(0).getPointOnLine(t);

            synchronized (lockGui) {
                ConvertBitmap.grayToBitmap(gray, bitmap, storage);

                lines.reset();
                for (LineParametric2D_F32 p : found) {
                    LineSegment2D_F32 ls = LineImageOps.convert(p, gray.width, gray.height);
                    lines.grow().set(ls.a, ls.b);

                }
            }
        }
    }


    @Override
    protected void render(Canvas canvas, double imageToOutput) {
        if(cam != null && !DEBUG)
            canvas.drawBitmap(cam, 0, 0, null);
        else
            canvas.drawBitmap(bitmap, 0, 0, null);

        LineSegment2D_F32[] l = new LineSegment2D_F32[2];
        l[0] = lines.toList().get(0);
        l[1] = lines.toList().get(1);



        Path p = new Path();
        p.moveTo(l[0].a.x, l[0].a.y);
        p.lineTo(intersection.x, intersection.y);
        p.lineTo(l[1].a.x, l[1].a.y);
        p.close();

        for (LineSegment2D_F32 s : lines.toList()) {
            canvas.drawLine(s.a.x, s.a.y, s.b.x, s.b.y, paint);
        }
        canvas.drawPath(p, roadPaint);
    }

    @Override
    public void convertPreview(byte[] bytes, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuv = new YuvImage(bytes, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

        byte[] b = out.toByteArray();
        cam = BitmapFactory.decodeByteArray(b, 0, b.length);

        super.convertPreview(bytes, camera);
    }

    /**
     * Finds a pair of lines that match the desired angle with some tolerance (both measurements in degrees).
     * Default is 25 degrees +- 10 degrees
     * @param found List of lines to search through
     */
    private void filterNearlyDesiredAngles(List<LineParametric2D_F32> found, double angle, double tolerance) {

        //convert to radians
        angle = Math.toRadians(angle);
        tolerance = Math.toRadians(tolerance);

        List<LineParametric2D_F32> toRemove = new LinkedList<>();
        for (LineParametric2D_F32 p : found) {
            boolean pair = false;
            for (LineParametric2D_F32 s : found) {
                if(!p.equals(s)
                        && Math.abs(p.getAngle() - s.getAngle()) < angle+tolerance
                        && Math.abs(p.getAngle() - s.getAngle()) > angle-tolerance) {
                    pair = true;
                }
            }
            if(!pair) toRemove.add(p);
        }

        found.removeAll(toRemove);
    }
}
