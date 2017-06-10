package com.weatherapp.fragments.weathermanagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.weatherapp.R;
import com.weatherapp.common.AppConstants;
import com.weatherapp.common.AppSettings;
import com.weatherapp.common.LocationCallback;
import com.weatherapp.datamodel.today.CurrentWeather;
import com.weatherapp.http.AppWebservices;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment implements AppWebservices.AppWebServiceCallBack, LocationCallback {

    private static final String TAG = "TodayWeatherFragment";

    private String temprature = "0" + (char) 0x00B0;

    private CurrentWeather currentWeather;
    private AppWebservices appWebservices;

    //UI Reference
    private AppCompatImageView imgWeatherIcon;
    private AppCompatTextView txtAddress,txtTemprature, txtSunRise, txtSunSet;
    private AppCompatTextView txtDateToday, txtWeatherToday;

    public TodayWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWebservices = new AppWebservices(getActivity(), getContext(), this, "weather_today", AppConstants.WEATHER_API_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        imgWeatherIcon = (AppCompatImageView) view.findViewById(R.id.img_weather_icon);

        txtDateToday = (AppCompatTextView) view.findViewById(R.id.txt_date_today);
        txtAddress = (AppCompatTextView) view.findViewById(R.id.txt_address);
        txtTemprature = (AppCompatTextView) view.findViewById(R.id.txt_temprature);
        txtTemprature.setText(temprature);

        txtSunRise = (AppCompatTextView) view.findViewById(R.id.txt_sunrise);
        txtSunSet = (AppCompatTextView) view.findViewById(R.id.txt_sunset);
        txtWeatherToday = (AppCompatTextView) view.findViewById(R.id.txt_weather_today);

        if (AppConstants.LOCATION_ADDRESS.isEmpty()) {
            AppSettings.getLocation((AppCompatActivity) getActivity(), TodayWeatherFragment.this);
        }else {
            getTodaysWeather(AppConstants.LOCATION_ADDRESS);
            txtAddress.setText(AppConstants.LOCATION_ADDRESS);
        }
        return view;
    }

    private void getTodaysWeather(String address) {
        try {
            appWebservices.execute("{\"{ADDRESS}\" : " + URLEncoder.encode(address, "utf-8") + ",\"{APIKEY}\" : " + AppConstants.OPEN_WEATHER_API_KEY + "}");
        }catch (Exception e){
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onSuccess(JSONObject response, String key) {
        Log.i(TAG, "Weather report: " + response);
        try {
            currentWeather =( new Gson()).fromJson(String.valueOf(response), CurrentWeather.class);
            if (currentWeather != null) {
                if (currentWeather.getWeather() != null && currentWeather.getWeather().size() > 0) {
                    Picasso.with(getContext()).load(String.format(AppConstants.WEATHER_API_IMAGE_URL, currentWeather.getWeather().get(0).getIcon())).into(imgWeatherIcon);
                    txtWeatherToday.setText(currentWeather.getWeather().get(0).getDescription());
                }

                txtAddress.setText(AppConstants.LOCATION_ADDRESS);
                if (currentWeather.getMain() != null) {
                    txtTemprature.setText(String.format("%s%cC", currentWeather.getMain().getTemp(), (char) 0x00B0));
                }
                txtDateToday.setText(AppSettings.getFormattedDate(currentWeather.getDt()));
                if (currentWeather.getSys() != null) {
                    txtSunRise.setText(String.format("%s\n%s", AppSettings.getFormattedTime(currentWeather.getSys().getSunrise()), getContext().getString(R.string.sunrise)));
                    txtSunSet.setText(String.format("%s\n%s", AppSettings.getFormattedTime(currentWeather.getSys().getSunset()), getContext().getString(R.string.sunset)));
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onError(String message) {
        AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case AppConstants.LOCATION_PERMISSION_SUCCESS:
                //new LocationTrackingTask(this, this, this).execute();
                AppSettings.getLocation((AppCompatActivity) getActivity(), TodayWeatherFragment.this);
                break;
        }
    }

    @Override
    public void onLocationReceived() {
        getTodaysWeather(AppConstants.LOCATION_ADDRESS);
    }
}
