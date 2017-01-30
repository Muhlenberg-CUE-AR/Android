package muhlenberg.edu.cue.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import muhlenberg.edu.cue.MainActivity;

/**
 * Created by Jalal on 1/28/2017.
 */
public class CUELocationService extends AbstractService {

    private static CUELocationService instance;
    private Context context;
    private GoogleApiClient googleApiClient;

    public static CUELocationService getInstance(Context context) {
        if (instance == null)
            return new CUELocationService(context);

        return instance;
    }


    private CUELocationService(Context context) {
        this.context = context;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        LocationRequest locationRequest = new LocationRequest();

                        if (ActivityCompat.checkSelfPermission(CUELocationService.this.context,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(CUELocationService.this.context,
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                            LocationServices.FusedLocationApi
                                    .requestLocationUpdates(googleApiClient, locationRequest, (MainActivity) CUELocationService.this.context);

                            Toast.makeText(CUELocationService.this.context, "Connected to Play Services", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(CUELocationService.this.context, "Disconnected to Play Services", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(CUELocationService.this.context, "Failed to connect to Play Services", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

    }

    @Override
    public void start() {
        if(!this.googleApiClient.isConnected() && !this.googleApiClient.isConnecting())
            this.googleApiClient.connect();
    }

    @Override
    public void stop() {
        if(this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (MainActivity) this.context);
            this.googleApiClient.disconnect();
        }
    }
}
