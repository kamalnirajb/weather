package com.weatherapp.fragments.weathermanagement;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.weatherapp.R;
import com.weatherapp.adapter.WeatherAdapter;
import com.weatherapp.common.AppConstants;
import com.weatherapp.common.AppSettings;
import com.weatherapp.common.LocationCallback;
import com.weatherapp.datamodel.weatherforecast.Weatherinfo;
import com.weatherapp.http.AppWebservices;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherForecastFragment extends Fragment implements AppWebservices.AppWebServiceCallBack, LocationCallback {

    private static final String TAG = "WeatherForecastFragment";
    private AppWebservices appWebservices;
    private Weatherinfo weatherinfo;
    // UI Reference
    private RecyclerView recyclerViewWeatherList;
    private WeatherAdapter adapter;


    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appWebservices = new AppWebservices(getActivity(), getContext(), this, "weather_historyy", AppConstants.WEATHER_API_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_weather_forcast, container, false);
        recyclerViewWeatherList = (RecyclerView) view.findViewById(R.id.recycler_weathers);
        if (AppConstants.LOCATION_ADDRESS.isEmpty()) {
            AppSettings.getLocation((AppCompatActivity) getActivity(), WeatherForecastFragment.this);
        }else {
            getWeatherForecast(AppConstants.LOCATION_ADDRESS);
        }

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case AppConstants.LOCATION_PERMISSION_SUCCESS:
                //new LocationTrackingTask(this, this, this).execute();
                AppSettings.getLocation((AppCompatActivity) getActivity(), WeatherForecastFragment.this);
                break;
        }
    }

    private void getWeatherForecast(String address) {
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
            weatherinfo =( new Gson()).fromJson(String.valueOf(response), Weatherinfo.class);
            adapter = new WeatherAdapter(getContext(), weatherinfo.getList());
            recyclerViewWeatherList.setAdapter(adapter);
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onError(String message) {
        AppSettings.showAlert(getActivity().getSupportFragmentManager(),getContext(), message);
    }

    @Override
    public void onLocationReceived() {
        getWeatherForecast(AppConstants.LOCATION_ADDRESS);
    }
}
