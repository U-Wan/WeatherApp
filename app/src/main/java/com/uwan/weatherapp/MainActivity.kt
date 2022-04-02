package com.uwan.weatherapp

import android.graphics.Color;
import android.os.Build
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

class MainActivity : AppCompatActivity() {

   lateinit var etCity: EditText
   lateinit var etCountry:EditText
    var tvResult: TextView? = null
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val appid = "ad4b8c1c1a3315fdd473ce4b31bc5258"
    @RequiresApi(Build.VERSION_CODES.N)
    var df: DecimalFormat = DecimalFormat("#.##")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var adRequest = AdRequest.Builder().build();

        etCity = findViewById(R.id.etCity);

        tvResult = findViewById(R.id.tvResult);
    }

    open fun getWeatherDetails(view: View?) {
        var tempUrl = ""
        val city: String = etCity.getText().toString().trim()
        val country: String = etCountry.getText().toString().trim()
        if (city == "") {
            tvResult?.setText("City field can not be empty!")
        } else {
            tempUrl = if (country != "") {
                url.toString() + "?q=" + city + "," + country + "&appid=" + appid
            } else {
                url.toString() + "?q=" + city + "&appid=" + appid
            }
            val stringRequest =
                StringRequest(Request.Method.POST, tempUrl, object : Response.Listener<String?> {
                    override fun onResponse(response: String?) {
                        var output = ""
                        try {
                            val jsonResponse = JSONObject(response)
                            val jsonArray = jsonResponse.getJSONArray("weather")
                            val jsonObjectWeather = jsonArray.getJSONObject(0)
                            val description = jsonObjectWeather.getString("description")
                            val jsonObjectMain = jsonResponse.getJSONObject("main")
                            val temp = jsonObjectMain.getDouble("temp") - 273.15
                            val feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15
                            val pressure = jsonObjectMain.getInt("pressure").toFloat()
                            val humidity = jsonObjectMain.getInt("humidity")
                            val jsonObjectWind = jsonResponse.getJSONObject("wind")
                            val wind = jsonObjectWind.getString("speed")
                            val jsonObjectClouds = jsonResponse.getJSONObject("clouds")
                            val clouds = jsonObjectClouds.getString("all")
                            val jsonObjectSys = jsonResponse.getJSONObject("sys")
                            val countryName = jsonObjectSys.getString("country")
                            val cityName = jsonResponse.getString("name")
                            tvResult?.setTextColor(Color.rgb(68, 134, 199))
                            output += """Current weather of $cityName ($countryName)
                                Temp: ${df.format(temp)} °C
                             Feels Like: ${df.format(feelsLike)} °C
                         Humidity: $humidity%
                         Description: $description
                                        Wind Speed: ${wind}m/s (meters per second)
                             Cloudiness: $clouds%
                                                 Pressure: $pressure hPa"""
                            tvResult?.setText(output)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(
                            applicationContext,
                            error.toString().trim(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
            requestQueue.add(stringRequest)
        }
    }
}