package muhlenberg.edu.cue.customobjects;

import com.beyondar.android.opengl.renderable.Renderable;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;

/**
 * Created by Jalal on 3/17/2017.
 */

public class RoadObject extends GeoObject {
    public RoadObject(long id) {
        super(id);
        setVisible(true);
    }

    public RoadObject() {
        super();
        setVisible(true);
    }

    @Override
    protected Renderable createRenderable() {
        return RoadRenderable.getInstance();
    }
}
