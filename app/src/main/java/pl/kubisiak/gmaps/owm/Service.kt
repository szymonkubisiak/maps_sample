package pl.kubisiak.gmaps.owm

import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface OWMService {
    @GET("weather")
    fun getWeather(
        @Query("lat") lat: Double
        , @Query("lon") lon: Double
        , @Query("appid") appid: String = "b6907d289e10d714a6e88b30761fae22"
        , @Query("units") units: String? = null
    ): Single<WeatherResponse>
}

fun createOWMService(): OWMService? {
    val retrofit = createRetrofit()
    val retval = retrofit.create<OWMService>(OWMService::class.java)
    return retval
}

fun createRetrofit(): Retrofit {
    val retval = Retrofit.Builder()
        .baseUrl("https://samples.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(createOkHttpClient())
        .build()

    return retval
}


fun createOkHttpClient(): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
//    httpClient.addInterceptor { chain ->
//        val original = chain.request()
//        val originalHttpUrl = original.url()
//        val url = originalHttpUrl.newBuilder()
//            .addQueryParameter("username", "demo")
//            .build()
//        val requestBuilder = original.newBuilder()
//            .url(url)
//        val request = requestBuilder.build()
//        chain.proceed(request)
//    }
    return httpClient.build()
}

/*

Pole tekstowe z informacją pogodową tj.
nazwa miejscowości,
temperaturę w stopniach Celsjusza oraz
opis pogody;

https://samples.openweathermap.org/data/2.5/weather?lat=38&lon=139&appid=b6907d289e10d714a6e88b30761fae22

{
  "coord": {
    "lon": 139.01,
    "lat": 35.02
  },
  "weather": [
    {
      "id": 800,
      "main": "Clear",
      "description": "clear sky",
      "icon": "01n"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 285.514,
    "pressure": 1013.75,
    "humidity": 100,
    "temp_min": 285.514,
    "temp_max": 285.514,
    "sea_level": 1023.22,
    "grnd_level": 1013.75
  },
  "wind": {
    "speed": 5.52,
    "deg": 311
  },
  "clouds": {
    "all": 0
  },
  "dt": 1485792967,
  "sys": {
    "message": 0.0025,
    "country": "JP",
    "sunrise": 1485726240,
    "sunset": 1485763863
  },
  "id": 1907296,
  "name": "Tawarano",
  "cod": 200
}
 */