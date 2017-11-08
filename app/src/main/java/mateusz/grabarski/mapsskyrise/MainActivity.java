package mateusz.grabarski.mapsskyrise;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mateusz.grabarski.mapsskyrise.adapters.PlacesAdapter;
import mateusz.grabarski.mapsskyrise.adapters.listeners.PlaceListener;
import mateusz.grabarski.mapsskyrise.impl.MapsPresenterImpl;
import mateusz.grabarski.mapsskyrise.models.Result;
import mateusz.grabarski.mapsskyrise.utils.DialogsUtils;
import mateusz.grabarski.mapsskyrise.widgets.CustomInfoWindowAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

import static mateusz.grabarski.mapsskyrise.Constants.COURSE_LOCATION;
import static mateusz.grabarski.mapsskyrise.Constants.DEFAULT_ZOOM;
import static mateusz.grabarski.mapsskyrise.Constants.FINE_LOCATION;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        MapsContract.View {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @BindView(R.id.activity_main_no_items_place_holder)
    CardView noItemsPlaceHolder;

    @BindView(R.id.activity_main_places_rv)
    RecyclerView placesRv;

    private MapsContract.Presenter mPresenter;
    private GoogleMap mMap;
    private PlacesAdapter mAdapter;
    private List<Result> mPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MapsPresenterImpl(this, this);
        mPlaces = new ArrayList<>();

        mAdapter = new PlacesAdapter(this, mPlaces, (PlaceListener) mPresenter);
        placesRv.setLayoutManager(new LinearLayoutManager(this));
        placesRv.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isGoogleServicesOk())
            MainActivityPermissionsDispatcher.initMapWithPermissionCheck(this);
    }

    public boolean isGoogleServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({FINE_LOCATION, COURSE_LOCATION})
    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_map_fragment);
        mapFragment.getMapAsync(this);
    }

    @OnPermissionDenied({FINE_LOCATION, COURSE_LOCATION})
    public void showDeniedForCamera() {
        DialogsUtils.getMessageDialog(this, R.string.denine_location_permissions).show();
    }

    @OnNeverAskAgain({FINE_LOCATION, COURSE_LOCATION})
    public void showNeverAskForCamera() {
        DialogsUtils.getMessageDialog(this, R.string.never_ask_again_location_permissions).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_main_activity_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText))
                    mPresenter.searchPlaces(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_activity_my_location:
                mPresenter.loadCurrentLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mPresenter.mapIsReady();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void setDeviceLocation(boolean permission, LatLng latLng) {

        if (permission) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            moveCamera(latLng);
        }
    }

    @Override
    public void moveCamera(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    @Override
    public void showEmptyPlaceHolder() {
        placesRv.setVisibility(View.GONE);
        noItemsPlaceHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void showList() {
        placesRv.setVisibility(View.VISIBLE);
        noItemsPlaceHolder.setVisibility(View.GONE);
    }

    @Override
    public void displayPlaces(List<Result> places) {
        refreshList(places);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this));
        mMap.clear();

        refreshMapMarkers(places);
    }

    private void refreshList(List<Result> places) {
        mPlaces.clear();
        mPlaces.addAll(places);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshMapMarkers(List<Result> places) {

        for (Result result : places) {
            String info = getString(R.string.name_info) + result.getName() + "\n" +
                    getString(R.string.address_info) + result.getVicinity() + "\n";

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
            markerOptions.title(result.getName());
            markerOptions.snippet(info);

            mMap.addMarker(markerOptions);
        }
    }
}
