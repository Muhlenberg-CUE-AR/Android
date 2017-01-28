package muhlenberg.edu.cue;

import android.app.Application;
import android.content.Context;

import org.artoolkit.ar.base.assets.AssetHelper;

/**
 * Created by Jalal on 1/28/2017.
 */
public class ARSimpleApplication extends Application {

    private static Application instance;
    private static Context context;


    public static Application getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this.getContext();
        ((ARSimpleApplication) instance).initializeInstance();
    }


    protected void initializeInstance() {
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getInstance(), "Data");
    }
}
