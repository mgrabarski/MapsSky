package mateusz.grabarski.mapsskyrise;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public interface MapsContract {

    interface View {
        void setDeviceLocation(boolean permission, LatLng latLng);

        void moveCamera(LatLng latLng);
    }

    interface Presenter {
        void loadCurrentLocation();
        void mapIsReady();
        void searchPlaces(String text);
    }
}
