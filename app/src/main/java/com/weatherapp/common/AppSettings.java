package com.weatherapp.common;

import android.content.Context;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.weatherapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by NirajKumar on 6/9/17.
 */

public class AppSettings {

    private static final String TAG = "AppSettings";

    public static void showAlert(FragmentManager fragmentManager,  Context context, String message) {
        AppAlert appAlert = new AppAlert();
        Bundle bundle = new Bundle();
        bundle.putString("title", context.getString(R.string.app_name));
        bundle.putString("message", message);
        appAlert.setArguments(bundle);
        appAlert.show(fragmentManager,message);
    }

    /**
     * Get Formatted Time
     * @param milliseconds
     * @return
     */
    public static String getFormattedTime (long milliseconds) {
        if (milliseconds != 0) {
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date(milliseconds * 1000);
            return formatter.format(date);
        }
        return "";
    }

    /**
     * Get Formatted Date
     * @param milliseconds
     * @return
     */
    public static String getFormattedDate (long milliseconds) {
        if (milliseconds != 0) {
            DateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm");
            Date date = new Date(milliseconds * 1000);
            return formatter.format(date);
        }
        return "";
    }

     /**
     * Get the current location
     */
    public static synchronized void getLocation(AppCompatActivity appCompatActivity, LocationCallback callback) {

        LocationTracker locationTracker = new LocationTracker(appCompatActivity, appCompatActivity.getApplicationContext());
        if (locationTracker.canGetLocation()) {

            LocationManager locationManager = (LocationManager)appCompatActivity.getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.d("Your Location", "latitude:" + locationTracker.getLatitude()
                        + ", longitude: " + locationTracker.getLongitude());

                AppConstants.LOCATION_LONGITUDE = locationTracker.getLongitude();
                AppConstants.LOCATION_LATITUDE = locationTracker.getLatitude();
                List<Address> addresses = locationTracker.getAddress();
                if (addresses != null) {
                    StringBuilder sb = new StringBuilder();
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        /*for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            if (address.getAddressLine(i) != null) {
                                sb.append(address.getAddressLine(i)).append("\n");
                            }
                        }*/
                        if (address.getLocality() != null) {
                            sb.append(address.getLocality()).append("\n ");
                        }
                        if (address.getCountryName() != null) {
                            sb.append(address.getCountryName());
                        }
                    }
                    AppConstants.LOCATION_ADDRESS = String.valueOf(sb);
                    Log.i(TAG, "Latitude: " + AppConstants.LOCATION_LATITUDE + " Longitude: " + AppConstants.LOCATION_LONGITUDE + " Address: " + AppConstants.LOCATION_ADDRESS);
                    if (callback != null) {
                        callback.onLocationReceived();
                    }
                }

            }
        }
    }
}
