package muhlenberg.edu.cue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.world.World;

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
}
