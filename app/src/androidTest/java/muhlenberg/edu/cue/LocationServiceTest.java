package muhlenberg.edu.cue;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Jalal on 2/11/2017.
 */

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    GoogleApiClient client;

    public LocationServiceTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Before
    public void setUp() throws InterruptedException {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        this.activity = getActivity();
        final CountDownLatch latch = new CountDownLatch(1);

        client = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        latch.countDown();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        latch.countDown();
                    }
                })
                .build();

        client.connect();
        latch.await();
    }

    @SmallTest
    public void testPreconditions() {
        assertNotNull("MainActivity is not null", activity);
    }

    @Test
    public void testLocationRuntimePermissionsGranted() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override public void run() {
                assertEquals("NO GPS Permission Granted", PackageManager.PERMISSION_GRANTED,
                        ContextCompat.checkSelfPermission(activity,
                                android.Manifest.permission.ACCESS_FINE_LOCATION));
            }
        });
    }

    @Test
    public void testGoogleApiClientConnected() {
        assertEquals("Google api client not connected", true, client.isConnected());
    }


    @After
    public void tearDown() {
         client.disconnect();
    }




}
