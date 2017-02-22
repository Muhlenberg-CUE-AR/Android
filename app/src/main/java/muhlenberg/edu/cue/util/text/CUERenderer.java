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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static muhlenberg.edu.cue.util.text.Square.COORDS_PER_VERTEX;

public class CUERenderer extends ARRenderer implements GLSurfaceView.Renderer {

    private GLText glText;
    private GLText largeText;
    private Context context;

    private String text;

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
        largeText = new GLText(gl, context.getAssets());
        // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
        glText.load("Roboto-Regular.ttf", 30, 2, 2);
        largeText.load("Roboto-Regular.ttf", 120, 2, 2);

    }

    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        // Set to ModelView mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();  //reset the current matrix

        // enable texture + alpha blending
        // NOTE: this is required for text rendering! we could incorporate it into
        // the GLText class, but then it would be called multiple times (which impacts performance).
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        glText.begin(1.0f, 1.0f, 1.0f, 1.0f);         // Begin Text Rendering (Set Color WHITE)
        glText.draw("Hello World!", 0, 0, 0);
        glText.end();

        glText.begin(0, 1.0f, 1.0f, 1.0f);
        glText.draw("Goodbye World!", 0, 100, 0);
        glText.end();

        largeText.begin(1.0f, 0, 1.0f, 1.0f);
        largeText.draw("Hello Large World!", -300, -100, 0);
        largeText.end();

        largeText.begin(1.0f, 1.0f, 0, 1.0f);
        largeText.draw("Goodbye Large World!", -400, 210, 0);
        largeText.end();


        gl.glDisable(GL10.GL_BLEND);
        gl.glDisable(GL10.GL_TEXTURE_2D);

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

}