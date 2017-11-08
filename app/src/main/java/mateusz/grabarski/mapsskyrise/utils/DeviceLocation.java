package mateusz.grabarski.mapsskyrise.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class DeviceLocation {

    private static final String TAG = "DeviceLocation";

    private Context mContext;
    private DeviceLocationListener mListener;
    private LatLng mDeviceLocation;

    public DeviceLocation(Context context, DeviceLocationListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void refreshLocation(boolean notify) {
        getDeviceLocation(notify);
    }

    private void getDeviceLocation(final boolean notify) {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        try {
            if (checkLocationPermissions()) {
                Task locationTask = mFusedLocationProviderClient.getLastLocation();
                locationTask.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location location = (Location) task.getResult();

                            mDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            if (notify)
                                notifyListener();
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceCurrentLocation: " + e.getMessage());
        }
    }

    public boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public LatLng getDeviceLocation() {
        if (mDeviceLocation == null)
            refreshLocation(false);

        return mDeviceLocation;
    }

    private void notifyListener() {
        if (mListener != null && mDeviceLocation != null)
            mListener.onDeviceLocationReady(mDeviceLocation);
    }

    public interface DeviceLocationListener {
        void onDeviceLocationReady(LatLng latLng);
    }
}
