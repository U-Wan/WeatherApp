package com.uwan.weatherapp


import android.os.Bundle;
import android.view.View;
import android.widget.Button
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

class MainActivity : AppCompatActivity() {

    lateinit var btnshow:Button
   lateinit var etCity: EditText
   lateinit var txtcityname:TextView
    var tvResult: TextView? = null
    private val url = "https://api.openweathermap.org/data/2.5/weather"
    private val appid = "ad4b8c1c1a3315fdd473ce4b31bc5258"

    var df: DecimalFormat = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etCity = findViewById(R.id.etCity);
        btnshow=findViewById(R.id.btnGet)
        tvResult = findViewById(R.id.tvResult);
        txtcityname=findViewById(R.id.textviewcityname)
        btnshow.setOnClickListener{ view->
            getWeatherDetails(view)
        }
    }

     fun getWeatherDetails(view: View?) {
        var tempUrl = ""
        val city: String = etCity.text.toString().trim()
        if (city == "") {
            tvResult?.setText("City field can not be empty!")
        } else {
            tempUrl ="$url?q=$city&appid=$appid"
            val stringRequest =
                StringRequest(Request.Method.POST, tempUrl,
                    { response ->
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
                            txtcityname?.text="current weather in $cityName: "
                            output += """
                                |Temp: ${df.format(temp)}  
                                |Humidity: $humidity% 
                                |Description: $description 
                                |Cloudiness: $clouds% 
                                |Wind Speed: ${wind}m/s
                                |Pressure: $pressure hPa""".trimMargin()
                            tvResult?.text = output
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }) { error ->
                    Toast.makeText(
                        applicationContext,
                        error.toString().trim(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            val requestQueue: RequestQueue = Volley.newRequestQueue(applicationContext)
            requestQueue.add(stringRequest)
        }
    }
}