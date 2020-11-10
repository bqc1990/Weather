package com.bqc1990.weather.Helper;

import android.accounts.NetworkErrorException;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.bqc1990.weather.Database.WeatherContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherNetwork {
    private static final String TAG = WeatherNetwork.class.getSimpleName();
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/daily";
    private static final String PARAM_Q = "q";
    private static final String PARAM_CNT = "cnt";
    private static final String PARAM_APPID = "appid";
    private static final int cnt = 16;
    private static final String appid = "924b5bdedafbb71d445ad6e442ceb317";
    private static final String OWM_CODE = "code";
    private static final String OWM_LIST = "list";
    private static final String OWM_TEMP = "temp";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_MAX = "max";
    private static final String OWM_MIN = "min";
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_SPEED = "speed";
    private static final String OWM_SUNRISE = "sunrise";
    private static final String OWM_SUNSET = "sunset";
    private static final String OWM_WEATHER_ID = "id";
    private static final String OWM_TIMEZONE = "timezone";
    private static final String OWM_CITY = "city";
    private static final String OWM_POPULATION = "population";
    private static final String UNIT_IMPERIAL = "imperial";
    private static final String UNIT_METRIC = "metric";


    public static URL buildURL(String location){
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_Q, location)
                .appendQueryParameter(PARAM_CNT, String.valueOf(cnt))
                .appendQueryParameter(PARAM_APPID, appid)
                .build();
        URL address = null;
        try {
            address = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static String getResponseFromHttp(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if(scanner.hasNext()) return scanner.next();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "something wrong with getResponseFromHttp method.");
        }
        return null;
    }

    public static ContentValues[] covertJsonToContentValues(Context context, String JsonData) throws JSONException, NetworkErrorException {
        JSONObject jsonDataObject = new JSONObject(JsonData);
        if(jsonDataObject.has(OWM_CODE)){
            int code = jsonDataObject.getInt(OWM_CODE);
            switch (code){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    Log.v(TAG, "not found.");
                    return null;
                default:
                    throw new NetworkErrorException("can't connect to server.");
            }
        }

        long timezone = jsonDataObject.getJSONObject(OWM_CITY).getLong(OWM_TIMEZONE);
        long population = jsonDataObject.getJSONObject(OWM_CITY).getLong(OWM_POPULATION);
        JSONArray listArray = jsonDataObject.getJSONArray(OWM_LIST);
        ContentValues[] values = new ContentValues[listArray.length()];
        long date = WeatherDate.getNormalizedUTCDateForToday();
        String unit = WeatherPreference.getPreferredUnit(context);
        for(int i = 0;i < listArray.length();i++){
            long dateInMillis;
            long weather_id;
            long sunrise;
            long sunset;
            int high = 0;
            int low = 0;
            double pressure;
            double humidity;
            double wind_speed;
            JSONObject dailyForecast = listArray.getJSONObject(i);
            dateInMillis = date + i * WeatherDate.DAY_IN_MILLIS;
            sunrise = dailyForecast.getLong(OWM_SUNRISE);
            sunset = dailyForecast.getLong(OWM_SUNSET);
            pressure = dailyForecast.getDouble(OWM_PRESSURE);
            humidity = dailyForecast.getDouble(OWM_HUMIDITY);
            wind_speed = dailyForecast.getDouble(OWM_SPEED);
            JSONObject temperatureObject = dailyForecast.getJSONObject(OWM_TEMP);

            if(unit.equals(UNIT_METRIC)){
                high = WeatherUnit.toCelsius(temperatureObject.getDouble(OWM_MAX));
                low = WeatherUnit.toCelsius(temperatureObject.getDouble(OWM_MIN));
            }else{
                high = WeatherUnit.toFahrenheit(temperatureObject.getDouble(OWM_MAX));
                low = WeatherUnit.toFahrenheit(temperatureObject.getDouble(OWM_MIN));
            }
            JSONArray weatherArray = dailyForecast.getJSONArray(OWM_WEATHER);
            JSONObject zero = weatherArray.getJSONObject(0);
            weather_id = zero.getLong(OWM_WEATHER_ID);
            ContentValues val = new ContentValues();
            val.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateInMillis);
            val.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather_id);
            val.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MAX, high);
            val.put(WeatherContract.WeatherEntry.COLUMN_TEMP_MIN, low);
            val.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            val.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            val.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, wind_speed);
            val.put(WeatherContract.WeatherEntry.COLUMN_SUNRISE, sunrise);
            val.put(WeatherContract.WeatherEntry.COLUMN_SUNSET, sunset);
            val.put(WeatherContract.WeatherEntry.COLUMN_TIMEZONE, timezone);
            val.put(WeatherContract.WeatherEntry.COLUMN_POPULATION, population);
            values[i] = val;
        }
        return values;
    }

}
