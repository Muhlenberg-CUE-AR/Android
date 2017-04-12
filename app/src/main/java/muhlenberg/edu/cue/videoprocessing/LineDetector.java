package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;

import org.ddogleg.struct.FastQueue;

import java.io.ByteArrayOutputStream;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.line.LineImageOps;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoRenderProcessing;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageType;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;

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
    private Paint road = new Paint();


    public final static boolean DEBUG = false;
    private CannyEdge<GrayU8, GrayS16> canny;

    public LineDetector(DetectLine<GrayU8> detector) {
        super(ImageType.single(GrayU8.class));
        this.detector = detector;

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        road.setStyle(Paint.Style.FILL);
        road.setColor(Color.argb(30, 0, 0, 200));

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
            canny.process(gray, 0.1f, 0.3f, null);
            VisualizeImageData.drawEdgeContours(canny.getContours(), 0xFF0000, bitmap, storage);
            ConvertBitmap.bitmapToGray(bitmap, gray, null);
            List<LineParametric2D_F32> found = detector.detect(gray);

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
        if (cam != null && !DEBUG)
            canvas.drawBitmap(cam, 0, 0, null);
        else
            canvas.drawBitmap(bitmap, 0, 0, null);

        if (lines.toList().isEmpty() || lines.size() < 2)
            return;

        LineSegment2D_F32 l1 = lines.get(0);
        LineSegment2D_F32 l2 = lines.get(1);
        Path path = new Path();
        path.moveTo(l1.a.x, l1.a.y);
        path.lineTo(l1.b.x, l1.b.y);
        path.lineTo(l2.a.x, l2.a.y);
        path.close();

        RectF rb = new RectF();
        path.computeBounds(rb, false);

        Bitmap subregion = Bitmap.createBitmap(bitmap,(int) rb.left, (int)rb.top, (int)rb.width(), (int)rb.height());
        Bitmap scaled = Bitmap.createScaledBitmap(subregion, 1, 1, false);
        int color = scaled.getPixel(0 ,0);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        for(int i=0; i<subregion.getWidth(); i++) {
            for(int j=0; j<subregion.getHeight(); j++) {
                if(subregion.getPixel(i, j) <= Color.rgb(r,g,b)+25) {
                    subregion.setPixel(i,j,road.getColor());
                }
            }
        }

        canvas.drawBitmap(subregion, (int)rb.left, (int)rb.top, null);
        if (DEBUG) {
            for (LineSegment2D_F32 s : lines.toList()) {
                canvas.drawLine(s.a.x, s.a.y, s.b.x, s.b.y, paint);
            }
        }
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

}
