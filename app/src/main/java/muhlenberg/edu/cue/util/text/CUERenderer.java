// This is based on the OpenGL ES 1.0 sample application from the Android Developer website:
// http://developer.android.com/resources/tutorials/opengl/opengl-es10.html

package muhlenberg.edu.cue.util.text;

import android.annotation.TargetApi;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static muhlenberg.edu.cue.util.text.Square.COORDS_PER_VERTEX;

public class CUERenderer extends ARRenderer implements GLSurfaceView.Renderer {

    private GLText glText;
    private Context context;

    private Cube cube = new Cube(40.0f, 0.0f, 0.0f, 20.0f);

    private String text;
    // square to draw to the screen
    private Square mSquare;

    public CUERenderer(Context context) {
        super();
        this.context = context;
        this.text = "Hello world";
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

        // initialize Square to be drawn
        mSquare = new Square();
    }

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
        glText.draw(this.text, 0, 0, 0);
        glText.end();

        gl.glDisable(GL10.GL_BLEND);                  // Disable Alpha Blend
        gl.glDisable(GL10.GL_TEXTURE_2D);             // Disable Texture Mapping

        //mSquare.draw();     // Draws the square to the screen
        draw(gl);
    }

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

    public void setText(String text) {
        this.text = text;
    }

    @TargetApi(21)
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CW);

        cube.draw(gl);
    }

}