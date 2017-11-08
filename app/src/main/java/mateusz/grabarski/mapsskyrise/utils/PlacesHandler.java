package mateusz.grabarski.mapsskyrise.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mateusz.grabarski.mapsskyrise.Constants;
import mateusz.grabarski.mapsskyrise.R;
import mateusz.grabarski.mapsskyrise.models.MatchToday;
import mateusz.grabarski.mapsskyrise.models.Result;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static mateusz.grabarski.mapsskyrise.Constants.SEARCH_LIMIT;

/**
 * Created by MGrabarski on 07.11.2017.
 */

public class PlacesHandler {

    private static final String TAG = "PlacesHandler";

    private Context mContext;
    private OkHttpClient mOkHttpClient;
    private PlacesListener mPlacesListener;

    public PlacesHandler(Context mContext, PlacesListener mPlacesListener) {
        this.mContext = mContext;
        this.mPlacesListener = mPlacesListener;
        mOkHttpClient = new OkHttpClient();
    }

    public void searchPlaces(LatLng latLng, String text) {
        String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" +
                latLng.latitude +
                "," +
                latLng.longitude +
                "&radius=" +
                Constants.SEARCHING_RADIUS +
                "&keyword=" + text +
                "&sensor=true&key=" + mContext.getString(R.string.google_places_API_key);

        Log.d(TAG, "searchPlaces: " + url);

        new GooglePlacesTask().execute(url);
    }

    private class GooglePlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                return getJson(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            MatchToday matchToday = new Gson().fromJson(s, MatchToday.class);

            if (matchToday != null && matchToday.getResults() != null)
                mPlacesListener.onPlacesLoaded(getResultsByLimit(matchToday.getResults()));
            else
                mPlacesListener.onPlacesLoaded(Collections.<Result>emptyList());
        }
    }

    private List<Result> getResultsByLimit(List<Result> results) {
        List<Result> limited = new ArrayList<>();

        if (results.size() <= SEARCH_LIMIT)
            return results;

        for (int i = 0; i < SEARCH_LIMIT; i++) {
            limited.add(results.get(i));
        }

        return limited;
    }

    private String getJson(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public interface PlacesListener {
        void onPlacesLoaded(List<Result> places);
    }
}
