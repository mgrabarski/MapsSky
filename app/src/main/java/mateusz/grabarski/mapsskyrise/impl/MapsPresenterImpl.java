package mateusz.grabarski.mapsskyrise.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import mateusz.grabarski.mapsskyrise.MapsContract;
import mateusz.grabarski.mapsskyrise.utils.DeviceLocation;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class MapsPresenterImpl implements MapsContract.Presenter, DeviceLocation.DeviceLocationListener, GoogleApiClient.OnConnectionFailedListener {

    private MapsContract.View mView;
    private DeviceLocation mDeviceLocation;
    private GoogleApiClient mGoogleApiClient;

    public MapsPresenterImpl(AppCompatActivity activity, MapsContract.View mView) {
        this.mView = mView;
        mDeviceLocation = new DeviceLocation(activity, this);
        mDeviceLocation.refreshLocation(false);

        mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(activity, this)
                .build();
    }

    @Override
    public void loadCurrentLocation() {
        mDeviceLocation.refreshLocation(true);
    }

    @Override
    public void mapIsReady() {
        mDeviceLocation.refreshLocation(true);
    }

    @Override
    public void searchPlaces(String text) {

    }

    @Override
    public void onDeviceLocationReady(LatLng latLng) {
        mView.setDeviceLocation(mDeviceLocation.checkLocationPermissions(), latLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
