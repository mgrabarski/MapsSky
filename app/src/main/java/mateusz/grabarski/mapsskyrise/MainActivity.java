package mateusz.grabarski.mapsskyrise;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import mateusz.grabarski.mapsskyrise.impl.MapsPresenterImpl;
import mateusz.grabarski.mapsskyrise.utils.DialogsUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

import static mateusz.grabarski.mapsskyrise.Constants.COURSE_LOCATION;
import static mateusz.grabarski.mapsskyrise.Constants.DEFAULT_ZOOM;
import static mateusz.grabarski.mapsskyrise.Constants.FINE_LOCATION;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, MapsContract.View {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private MapsContract.Presenter mPresenter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MapsPresenterImpl(this, this);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // TODO: 07.11.2017 search places by string
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
}
