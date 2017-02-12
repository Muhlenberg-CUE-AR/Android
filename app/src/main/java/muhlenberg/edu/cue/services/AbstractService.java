package muhlenberg.edu.cue.services;

import android.content.Context;

/**
 * Created by Jalal on 1/27/2017.
 */
public abstract class AbstractService {
    public abstract void start(Context context);
    public abstract void stop(Context context);
}
