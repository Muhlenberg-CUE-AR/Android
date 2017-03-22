package muhlenberg.edu.cue.services.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import muhlenberg.edu.cue.MainActivity;
import muhlenberg.edu.cue.services.AbstractService;

/**
 * Created by Jalal on 1/28/2017.
 *
 * 2/28/17
 * TODO: figure out how to not need context twice without a field instance
 */
public class CUELocationService extends AbstractService {

    /**
     * Singleton pattern
     */
    private static CUELocationService instance;

    /**
     * Google API Client used to connect to LocationServices
     * @see <a href="https://developer.android.com/training/location/receive-location-updates.html"
     */
    private GoogleApiClient googleApiClient;

    public static boolean DEBUG = false;

    public static CUELocationService getInstance(Context context) {
        if (instance == null)
            instance = new CUELocationService(context);

        return instance;
    }

    /**
     * Create a default CUELocationService if the singleton is being initialized
     * Creates a Google API Client to connect to for Location Services, it should only be created
     * once. After which, start() and stop() will maintain state
     * @param context
     */
    private CUELocationService(final Context context) {
        this.googleApiClient = new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(createConnectionCallbacks(context))
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    Toast.makeText(context, "Failed to connect to Play Services", Toast.LENGTH_SHORT).show();
                }
                })
            .build();

    }

    /**
     * Use Google API Client to connect to Location Services
     * It should already be initialized and should only connect if it is not already doing so or
     * is already connected.
     *
     */
    @Override
    public void start(Context context) {
        if(!this.googleApiClient.isConnected() && !this.googleApiClient.isConnecting())
            this.googleApiClient.connect();
        else {
            Log.d("cuear", "Attempting to connect to play services when already connecting or connected. Are there duplicate calls to start()?");
        }
    }

    /**
     * Remove any listeners from Google API Client and disconnect it.
     * This is not a "pause" and should be used sparingly.
     */
    @Override
    public void stop(Context context) {
        if(this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (MainActivity) context);
            this.googleApiClient.disconnect();
        }
        else {
            Log.d("cuear", "Play services is already disconnected! Was connection lost, or are there duplicate calls to stop()?");
        }
    }

    /**
     * Create a new ConnectionCallback for the Google API Client
     * The callback decides what to do once we are connected to Google API
     * We want to request location, so we build a LocationRequest {@link #buildLocationRequest() }
     * We simply log any connection suspensions
     * @return GoogleApiClient.ConnectionCallbacks
     */
    private GoogleApiClient.ConnectionCallbacks createConnectionCallbacks(final Context context) {
        return new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                LocationRequest locationRequest = buildLocationRequest();

                //Check for permission before accessing location
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, (MainActivity) context);

                    Log.d("cuear", "Connected to Play Services");
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("cuear", "Google API Location Services suspended");
            }
        };
    }

    /**
     * Create a LocationRequest for the Google API Client
     * LocationRequest defines the options we want to update our location at
     * By default, we would like to balance power and accuracy, and update every 10 seconds
     * @return LocationRequest
     */
    private LocationRequest buildLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        return locationRequest;
    }
}
