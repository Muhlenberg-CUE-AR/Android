// This is based on the OpenGL ES 1.0 sample application from the Android Developer website:
// http://developer.android.com/resources/tutorials/opengl/opengl-es10.html

package muhlenberg.edu.cue.util.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import muhlenberg.edu.cue.util.text.GLText;

public class CUERenderer extends ARRenderer {

    private GLText glText;
    private Context context;

    private String text;
    private int textX, textY;

    public CUERenderer(Context context) {
        super();
        this.context = context;
        this.text = "Hello World";
        this.textX = 0;
        this.textY = 0;
    }

    @Override
    public boolean configureARScene() {
        super.configureARScene();
        ARToolKit.getInstance().addMarker("single;Data/patt.hiro;80");

        return true;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        glText = new GLText(gl, context.getAssets());

        // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
        glText.load("Roboto-Regular.ttf", 14, 2, 2);

        Log.d("cuear", "gl surface created");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        // Set to ModelView mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // enable texture + alpha blending
        // NOTE: this is required for text rendering! we could incorporate it into
        // the GLText class, but then it would be called multiple times (which impacts performance).
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Set Alpha Blend Function

        glText.begin(1.0f, 1.0f, 1.0f, 1.0f);         // Begin Text Rendering (Set Color WHITE)
        glText.draw(this.text, this.textX, this.textY, 0);
        glText.end();

        gl.glDisable(GL10.GL_BLEND);                  // Disable Alpha Blend
        gl.glDisable(GL10.GL_TEXTURE_2D);             // Disable Texture Mapping
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int x, int y) {
        float fov_degrees = 45f;
        float fov_radians = fov_degrees / 180 * (float) Math.PI;
        float aspect = (float) x / (float) y;
        float camZ = y / 2 / (float) Math.tan(fov_radians / 2);

        //avoid division by 0
        x = x == 0 ? 1 : x;

        // reset viewport and projection matrix
        gl.glViewport(0, 0, x, y);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();


        // calculate aspect ratio
        GLU.gluPerspective(gl, fov_degrees, aspect, camZ / 10, camZ * 10);

        // move camera back
        GLU.gluLookAt(gl, 0, 0, camZ, 0, 0, 0, 0, 1, 0);

        // reset matrix to modelview
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void setText(String text, int x, int y) {
        this.text = text;
        this.textX = x;
        this.textY = y;
    }

}