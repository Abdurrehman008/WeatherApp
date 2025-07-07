package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText etCity;
    Button btnGetWeather;
    TextView tvResult;
    ImageView imgWeather;
    RecyclerView recyclerForecast;
    List<ForecastItem> forecastItems = new ArrayList<>();

    FusedLocationProviderClient fusedLocationClient;

    final String API_KEY = "5ecaddfd4f14ff3de91e50892e6de0e7"; // Replace with your OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        tvResult = findViewById(R.id.tvResult);
        imgWeather = findViewById(R.id.imgWeather);
        recyclerForecast = findViewById(R.id.recyclerForecast);
        recyclerForecast.setLayoutManager(new LinearLayoutManager(this));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnGetWeather.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherByCity(city);
                fetchForecast(city);
                etCity.setText(""); // Clear input
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        // Auto-fetch current location weather on startup
        getUserLocation();
    }

    void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                fetchWeatherByCoordinates(lat, lon);
            }
        });
    }

    void fetchWeatherByCity(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject main = json.getJSONObject("main");
                        JSONObject wind = json.getJSONObject("wind");

                        String temp = main.getString("temp");
                        String humidity = main.getString("humidity");
                        String windSpeed = wind.getString("speed");
                        String weather = json.getJSONArray("weather").getJSONObject(0).getString("main");
                        String cityName = json.getString("name");

                        String result = "City: " + cityName + "\n"
                                + "Temperature: " + temp + "°C\n"
                                + "Condition: " + weather + "\n"
                                + "Humidity: " + humidity + "%\n"
                                + "Wind Speed: " + windSpeed + " m/s";

                        tvResult.setText(result);
                        setWeatherIcon(weather);

                    } catch (Exception e) {
                        tvResult.setText("Error: " + e.getMessage());
                    }
                },
                error -> tvResult.setText("City not found or check internet.")
        );

        queue.add(request);
    }

    void fetchWeatherByCoordinates(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject main = json.getJSONObject("main");
                        JSONObject wind = json.getJSONObject("wind");

                        String temp = main.getString("temp");
                        String humidity = main.getString("humidity");
                        String windSpeed = wind.getString("speed");
                        String weather = json.getJSONArray("weather").getJSONObject(0).getString("main");
                        String cityName = json.getString("name");

                        String result = "City: " + cityName + "\n"
                                + "Temperature: " + temp + "°C\n"
                                + "Condition: " + weather + "\n"
                                + "Humidity: " + humidity + "%\n"
                                + "Wind Speed: " + windSpeed + " m/s";

                        tvResult.setText(result);
                        setWeatherIcon(weather);

                    } catch (Exception e) {
                        tvResult.setText("Error: " + e.getMessage());
                    }
                },
                error -> tvResult.setText("Failed to fetch location weather.")
        );

        queue.add(request);
    }

    void fetchForecast(String city) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray list = json.getJSONArray("list");
                        forecastItems.clear();

                        for (int i = 0; i < list.length(); i += 8) {
                            JSONObject obj = list.getJSONObject(i);
                            String date = obj.getString("dt_txt");
                            String temp = obj.getJSONObject("main").getString("temp") + " °C";
                            String condition = obj.getJSONArray("weather").getJSONObject(0).getString("main");

                            forecastItems.add(new ForecastItem(date, temp, condition));
                        }
                        recyclerForecast.setAdapter(new ForecastAdapter(forecastItems));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Forecast fetch failed", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    void setWeatherIcon(String weather) {
        switch (weather.toLowerCase()) {
            case "clear": imgWeather.setImageResource(R.drawable.ic_clear); break;
            case "clouds": imgWeather.setImageResource(R.drawable.ic_clouds); break;
            case "rain": imgWeather.setImageResource(R.drawable.ic_rain); break;
            case "snow": imgWeather.setImageResource(R.drawable.ic_snow); break;
            case "thunderstorm": imgWeather.setImageResource(R.drawable.ic_thunder); break;
            default: imgWeather.setImageResource(R.drawable.ic_weather_placeholder); break;
        }
    }
}
