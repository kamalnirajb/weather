package com.weatherapp.common;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by NirajKumar on 6/9/17.
 */

public class AppConstants {

    public static final  String WEATHER_API_URL = "http://api.openweathermap.org/";
    public static final String WEATHER_API_IMAGE_URL = "http://openweathermap.org/img/w/%s.png";

    public static final String APP_API_URL = "https://nirweatherapp.herokuapp.com/";
    public final static String OPEN_WEATHER_API_KEY = "d70a12f92cbb9f405c74d6c708bae42f";

    public static final String LOCATION_RECEIVED = "com.weatherapp.LOCATION_RECEIVED";

    public static final int SIGN_IN_SUCCESS = 200;
    public static final int LOCATION_PERMISSION_SUCCESS = 200;

    public static GoogleApiClient googleApiClient;
    public static String LOCATION_ADDRESS = "";
    public static double LOCATION_LATITUDE = 0.0;
    public static double LOCATION_LONGITUDE = 0.0;
}
