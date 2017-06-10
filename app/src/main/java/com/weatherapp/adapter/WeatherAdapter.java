package com.weatherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weatherapp.R;
import com.weatherapp.ViewHolder.WeatherViewHolder;
import com.weatherapp.datamodel.weatherforecast.ListItem;

import java.util.List;

/**
 * Created by NirajKumar on 6/11/17.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    private static final String TAG = "WeatherAdapter";

    private Context context;
    private List<ListItem> weatherItems;

    public WeatherAdapter(Context context, List<ListItem> weatherItems) {
        super();
        this.context = context;
        this.weatherItems = weatherItems;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_weather_item, parent, false);
        WeatherViewHolder recyclerViewHolder = new WeatherViewHolder(view,context);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder weatherViewHolder, int position) {
        try {
            weatherViewHolder.setData(weatherItems.get(position));
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return (weatherItems != null) ? weatherItems.size() : 0;
    }
}
