package muhlenberg.edu.cue.util.renderable;

import com.beyondar.android.opengl.renderable.Renderable;
import com.beyondar.android.opengl.texture.Texture;
import com.beyondar.android.util.math.geom.Plane;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.world.BeyondarObject;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jalal on 3/11/2017.
 */

public class RoadRenderable implements Renderable {

    public RoadRenderable() {
        super();
    }

    @Override
    public void draw(GL10 gl, Texture defaultTexture) {

    }

    @Override
    public boolean update(long time, double distance, BeyondarObject beyondarObject) {
        return false;
    }

    @Override
    public void onNotRendered(double dst) {

    }

    @Override
    public Texture getTexture() {
        return null;
    }

    @Override
    public Plane getPlane() {
        return null;
    }

    @Override
    public void setPosition(float x, float y, float z) {

    }

    @Override
    public Point3 getPosition() {
        return null;
    }

    @Override
    public void setAngle(float x, float y, float z) {

    }

    @Override
    public Point3 getAngle() {
        return null;
    }

    @Override
    public long getTimeMark() {
        return 0;
    }
}
