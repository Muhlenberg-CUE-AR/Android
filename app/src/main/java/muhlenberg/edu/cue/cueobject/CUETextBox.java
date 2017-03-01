package muhlenberg.edu.cue.cueobject;

/**
 * Created by jason on 2/28/17.
 */

import android.content.Context;
import android.location.Location;
import android.util.Log;

import muhlenberg.edu.cue.services.graphics.CUERendererService;
import muhlenberg.edu.cue.util.location.CUELocation;
import muhlenberg.edu.cue.util.location.CUELocationUtils;
import muhlenberg.edu.cue.util.renderer.CUERenderer;

public class CUETextBox extends CUEWorldObject {

    private String name = "";
    private String desc = "";
    private String color = "";
    private int sx;
    private int sy;

    public void  update(CUELocation loc, float azimuthRadians) {
        CUERenderer cueRenderer = CUERendererService.getInstance().getRenderer();
            if(azimuthRadians != Float.MAX_VALUE) {
                Log.d("cuear", "azimuth is updated with a value of " + azimuthRadians);
                double distance = CUELocationUtils.getDistance(location, loc);
                double angle = CUELocationUtils.getAngleAsDegrees(location, loc) * Math.PI / 180.0;
                double x = CUELocationUtils.getObjectScreenX(angle, azimuthRadians, distance);
                double y = CUELocationUtils.getObjectScreenY(angle, azimuthRadians, distance);

                sx = (int) Math.floor(x);
                sy = (int) Math.floor(y);
                draw();
            }
            else {
                cueRenderer.setText("azimuth: " + azimuthRadians, 0, 0);
            }

    }

    @Override
    public void draw() {
        CUERenderer cueRenderer = CUERendererService.getInstance().getRenderer();
        cueRenderer.setText(name + desc + " at " + sx + ", " + sy, sx, sy);
    }

}
