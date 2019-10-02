package com.pineapple.weatherapp.weatherapp.controller;

import com.pineapple.weatherapp.weatherapp.model.EnvModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Data
@Service
public class WeatherService {

    @Autowired
    EnvModel env;

    @Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PRIVATE) private OkHttpClient client;

    @Setter(AccessLevel.PUBLIC) @Getter(AccessLevel.PRIVATE) private Response response;


    private String cityName;
    private String unit;

    public JSONObject getWeather(){
        String appId = env.getWheatherapiid();
        System.out.println(appId);
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q="+getCityName()+"&units="+getUnit()+"&APPID="+appId)
                .build();
        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public JSONArray returnWeatherArray(){
        return  getWeather().getJSONArray("weather");
    }

    public JSONObject returnMainObject(){
        return getWeather().getJSONObject("main");
    }

    public JSONObject returnWindObject(){
        return getWeather().getJSONObject("wind");
    }

    public JSONObject returnSunSet(){
        return getWeather().getJSONObject("sys");
    }
}
