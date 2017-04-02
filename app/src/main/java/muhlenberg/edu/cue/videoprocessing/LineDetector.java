package muhlenberg.edu.cue.videoprocessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.ddogleg.struct.FastQueue;

import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.alg.feature.detect.line.LineImageOps;
import boofcv.android.ConvertBitmap;
import boofcv.android.gui.VideoRenderProcessing;
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
    private Paint paint = new Paint();

    public LineDetector(DetectLine<GrayU8> detector) {
        super(ImageType.single(GrayU8.class));
        this.detector = detector;

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

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
        canvas.drawBitmap(bitmap, 0, 0, null);

        for (LineSegment2D_F32 s : lines.toList()) {
            canvas.drawLine(s.a.x, s.a.y, s.b.x, s.b.y, paint);
        }

    }
}
