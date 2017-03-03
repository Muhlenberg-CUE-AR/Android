package muhlenberg.edu.cue.services.graphics;

import android.content.Context;

import muhlenberg.edu.cue.services.AbstractService;
import muhlenberg.edu.cue.util.renderer.CUERenderer;

/**
 * Created by Jalal on 2/28/2017.
 */

public class CUERendererService extends AbstractService {

    private static CUERendererService instance;
    private CUERenderer cueRenderer;

    public static CUERendererService getInstance() {
        if (instance == null)
            instance = new CUERendererService();

        return instance;
    }

    @Override
    public void start(Context context) {
        if(this.cueRenderer == null)
            this.cueRenderer = new CUERenderer(context);
    }

    @Override
    public void stop(Context context) {

    }

    public CUERenderer getRenderer() {
        return this.cueRenderer;
    }

    public void setRenderer(CUERenderer renderer) {
        this.cueRenderer = renderer;
    }
}
