package com.weatherapp.ViewHolder;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.weatherapp.R;
import com.weatherapp.common.AppConstants;
import com.weatherapp.common.AppSettings;
import com.weatherapp.datamodel.weatherforecast.ListItem;

/**
 * Created by NirajKumar on 6/11/17.
 */

public class WeatherViewHolder extends RecyclerView.ViewHolder {

    public AppCompatTextView txtDate, txtTemprature;
    public AppCompatImageView imgWeatherIcon;
    private Context context;
    public WeatherViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        txtDate = (AppCompatTextView) itemView.findViewById(R.id.txt_date);
        txtTemprature  = (AppCompatTextView) itemView.findViewById(R.id.txt_temprature);
        imgWeatherIcon = (AppCompatImageView) itemView.findViewById(R.id.img_weather_icon);
    }

    public void setData(ListItem listItem){
        if (listItem != null) {
            if (listItem.getWeather() != null && listItem.getWeather().size() > 0) {
                Picasso.with(context).load(String.format(AppConstants.WEATHER_API_IMAGE_URL, listItem.getWeather().get(0).getIcon())).into(imgWeatherIcon);
                // txtWeatherToday.setText(currentWeather.getWeather().get(0).getDescription());
            }

            if (listItem.getMain() != null) {
                txtTemprature.setText(String.format("%s%cC", listItem.getMain().getTemp(), (char) 0x00B0));
            }
            txtDate.setText(AppSettings.getFormattedDate(listItem.getDt()));
//            if (currentWeather.getSys() != null) {
//                txtSunRise.setText(String.format("%s\n%s", AppSettings.getFormattedTime(currentWeather.getSys().getSunrise()), context.getString(R.string.sunrise)));
//                txtSunSet.setText(String.format("%s\n%s", AppSettings.getFormattedTime(currentWeather.getSys().getSunset()), context.getString(R.string.sunset)));
//            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
