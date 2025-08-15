package com.example.weatherly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherly.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        fetchWeatherAppData("jaipur")
        searchCity()

    }
    private fun searchCity() {
        val searchView = findViewById<CustomSearchView>(R.id.customSearchView)
        searchView.setOnQueryTextListener(object : CustomSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchWeatherAppData(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }
    private fun fetchWeatherAppData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofit.getWeatherData(cityName,"4e80ec179dc79ae2f18c4df636089253","metric")
        retrofitData.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp?>, response: Response<WeatherApp?>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toInt()
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"

                    binding.temptext.text = "$temperature"
                    "Min : $minTemp ℃".also { binding.mintemp.text = it }
                    "Max : $maxTemp ℃".also { binding.maxtemp.text = it }
                    "$humidity %".also { binding.humiditytxt1.text = it }
                    "$windSpeed m/s".also { binding.windtxt1.text = it }
                    getTime(sunRise).also { binding.sunrisetxt1.text = it }
                    getTime(sunSet).also { binding.sunsettxt1.text = it }
                    "$seaLevel hpa".also { binding.seatxt1.text = it }
                    condition.also { binding.textView4.text = it }
                    condition.also { binding.raintxt1.text = it }
                    binding.daytext.text = getDay()
                    binding.datetext.text = getDate()
                    binding.textView.text = cityName

                    changeBackground(condition)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to Get Location", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherApp?>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Network Error : ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun getTime(timeStamp: Long): String {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
        return time.format((Date(timeStamp*1000)))
    }
    private fun changeBackground(condition: String) {
        when (condition) {
            "Clear Sky","Sunny","Clear" -> {
                binding.root.setBackgroundResource(R.drawable.desert)
                binding.imageView.setImageResource(R.drawable.sunny)
            }
            "Partly Clouds","Clouds","OverCast","Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.imageView.setImageResource(R.drawable.cloud_black)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rainy)
                binding.imageView.setImageResource(R.drawable.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.imageView.setImageResource(R.drawable.snow)
            }
            else -> {

            }
        }
    }
    private fun getDate(): String{
        val date = SimpleDateFormat("dd MMMM yyy", Locale.getDefault())
        return date.format((Date()))
    }
    private fun getDay(): String{
        val day = SimpleDateFormat("EEEE", Locale.getDefault())
        return day.format((Date()))
    }
}