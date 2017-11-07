package mateusz.grabarski.mapsskyrise.impl;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import mateusz.grabarski.mapsskyrise.MapsContract;
import mateusz.grabarski.mapsskyrise.models.Result;
import mateusz.grabarski.mapsskyrise.utils.DeviceLocation;
import mateusz.grabarski.mapsskyrise.utils.PlacesHandler;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class MapsPresenterImpl implements MapsContract.Presenter, DeviceLocation.DeviceLocationListener, PlacesHandler.PlacesListener {

    private MapsContract.View mView;
    private DeviceLocation mDeviceLocation;
    private PlacesHandler mPlacesHandler;

    public MapsPresenterImpl(AppCompatActivity activity, MapsContract.View mView) {
        this.mView = mView;
        mDeviceLocation = new DeviceLocation(activity, this);
        mDeviceLocation.refreshLocation(false);

        mPlacesHandler = new PlacesHandler(activity, this);
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
        mPlacesHandler.searchPlaces(mDeviceLocation.getDeviceLocation(), text);
    }

    @Override
    public void onDeviceLocationReady(LatLng latLng) {
        mView.setDeviceLocation(mDeviceLocation.checkLocationPermissions(), latLng);
    }

    @Override
    public void onPlacesLoaded(List<Result> places) {

    }
}
