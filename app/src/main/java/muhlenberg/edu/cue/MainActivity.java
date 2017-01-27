package muhlenberg.edu.cue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.beyondar.android.fragment.BeyondarFragmentSupport;

import muhlenberg.edu.cue.services.LocationService;

public class MainActivity extends AppCompatActivity {

    /**
     * Declare an AR fragment instance that will manage the primary display
     * This fragment will be the main process of the app, where the viewing, route highlighting,
     * and floating text is expected to render.
     *
     * It will instantiate with onCreate() and be destroyed with onStop()
     */
    private BeyondarFragmentSupport arFragment;

    /**
     * A LocationService reference to the singleton service that will manage the users current
     * location. This is expected to live and die with the app and will not run in the background.
     * Navigation and GPS signalling is expected to occur while the app is open and not outside it.
     */
    private LocationService locationService;

    /**
     * Entrypoint of the app. onCreate() should instantiate arFragment, any required services (GPS,
     * WiFi, etc), and load any necessary assets, like images or sound.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.arFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initServices();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }



    private void initServices() {
        locationService = LocationService.getInstance(MainActivity.this);
        locationService.start();
    }
}
