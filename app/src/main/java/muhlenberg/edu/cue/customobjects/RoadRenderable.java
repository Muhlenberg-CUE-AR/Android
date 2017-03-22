package muhlenberg.edu.cue.customobjects;

import com.beyondar.android.opengl.renderable.Renderable;
import com.beyondar.android.opengl.texture.Texture;
import com.beyondar.android.util.math.geom.Plane;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.util.math.geom.Vector3;
import com.beyondar.android.world.BeyondarObject;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jalal on 3/17/2017.
 *
 * Mostly a copy of SquareRenderable with the change
 */

public class RoadRenderable implements Renderable {
    private static RoadRenderable instance;

    private Road mCube = new Road();
    private float mCubeRotation;

    private Texture mTexture;
    private BeyondarObject mBeyondarObject;
    private Point3 mAngle;
    private Point3 mPosition;

    private long mTimeMark;

    private RoadRenderable() {
        mAngle = new Point3();
        mPosition = new Point3();
        mTexture = new Texture();
        mCubeRotation = 90.0f;
    }

    public synchronized static RoadRenderable getInstance() {
        if(instance == null)
            instance = new RoadRenderable();

        return instance;
    }

    @Override
    public void draw(GL10 gl, Texture defaultTexture) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -10.0f);
        gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);

        mCube.draw(gl);

        gl.glLoadIdentity();
    }

    @Override
    public boolean update(long time, double distance, BeyondarObject beyondarObject) {
        mTimeMark = time;
        mBeyondarObject = beyondarObject;

        mPosition.x = mBeyondarObject.getPosition().x;
        mPosition.y = mBeyondarObject.getPosition().y;
        mPosition.z = mBeyondarObject.getPosition().z;

        mAngle.x = mBeyondarObject.getAngle().x;
        mAngle.y = mBeyondarObject.getAngle().y;
        mAngle.z = mBeyondarObject.getAngle().z;

        return false;
    }

    @Override
    public void onNotRendered(double dst) {

    }

    @Override
    public Texture getTexture() {
        return mTexture;
    }

    @Override
    public Plane getPlane() {
        Plane plane = new Plane(mBeyondarObject.getPosition(), new Vector3(0, -1, 0));
        return plane;

    }

    @Override
    public void setPosition(float x, float y, float z) {
        mBeyondarObject.getPosition().x = x;
        mBeyondarObject.getPosition().y = y;
        mBeyondarObject.getPosition().z = z;

    }

    @Override
    public Point3 getPosition() {
        if (mBeyondarObject == null) {
            return null;
        }
        return mBeyondarObject.getPosition();

    }

    @Override
    public void setAngle(float x, float y, float z) {
        mAngle.x = x;
        mAngle.y = y;
        mAngle.z = z;
    }

    @Override
    public Point3 getAngle() {
        return this.mAngle;
    }

    @Override
    public long getTimeMark() {
        return mTimeMark;
    }
}
