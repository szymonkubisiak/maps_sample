package pl.kubisiak.gmaps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_maps.*
import pl.kubisiak.gmaps.locationslist.SavedLocationsListActivity
import pl.kubisiak.gmaps.owm.OWMService
import pl.kubisiak.gmaps.owm.WeatherResponse
import pl.kubisiak.gmaps.owm.createOWMService
import pl.kubisiak.gmaps.persistence.MyDataBase
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


private const val PERMISSIONS_REQUEST_CODE = 11
private const val SAVEDLOCATION_REQUEST_CODE = 12

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val owmService: OWMService by lazy { createOWMService()!! }
    private val locationSubject = BehaviorSubject.create<Location>()

    override fun onDestroy() {
        super.onDestroy()
        theRealDisposer.dispose()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        savePosition.setOnClickListener {
            lastWeatherResponse?.also {
                val lat = it.coord?.lat
                val lon = it.coord?.lon
                val name = it.name
                val temp = it.main?.temp
                if (lat != null && lon != null && name != null && temp != null) {
                    disposer = Completable.create { _ ->
                        MyDataBase.getDatabase(this).courseAndNameModel().addData(SavedPosition(lat, lon, Date(), name, temp))
                    }
                        .subscribeOn(Schedulers.io())
                        .subscribe()
                }
            }
        }

        disposer = locationSubject
            .sample(20, TimeUnit.SECONDS)
            .subscribe(::updateLocation)

        disposer = locationSubject
            .take(1)
            .subscribe(::updateLocation)
    }

    private fun updateLocation(it: Location){
        disposer = owmService.getWeather(it.latitude, it.longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                lastWeatherResponse = it
                cityName.text = getString(R.string.main_screen_city, it.name)
                temperature.text = getString(R.string.main_screen_temp, it.main?.temp)
                weather.text = getString(R.string.main_screen_weather, it.weather?.firstOrNull()?.description)
            })
    }

    private var lastWeatherResponse: WeatherResponse? = null

    private val theRealDisposer = CompositeDisposable()
    private var disposer: Disposable?
        get() = throw Exception("WTF")
        set(value) {
            value?.also { theRealDisposer.add(it) }
        }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(checkPermissions(this)) {
            enterLocationTrackingMode()
        } else {
            requestPermissions()
        }
        // Add a marker in Sydney and move the camera
        val modlin = LatLng(52.45, 20.65)
        //mMap.addMarker(MarkerOptions().position(modlin).title("Lotnisko w Modlinie"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(modlin))
        mMap.setOnMyLocationChangeListener {
            locationSubject.onNext(it)
        }
    }

    private fun checkPermissions(ctx: Context): Boolean {
        val fine = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine && coarse
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val length = grantResults.size
            if (length > 0) {
                val grantResult = grantResults[0]
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    enterLocationTrackingMode()
                }
            }
        }
    }

    private fun enterLocationTrackingMode() {
        mMap.clear()
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun enterLocationDisplayMode(arg: SavedPosition) {
        mMap.isMyLocationEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false

        val coords = arg.position
        mMap.clear()
        mMap.addMarker(MarkerOptions()
            .position(coords)
            .title(arg.city)
            .snippet(
                getString(R.string.saved_item_temp_and_date, arg.temperature, arg.date.toString())
            )
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SAVEDLOCATION_REQUEST_CODE)
            if (resultCode == RESULT_OK && data != null) {
                val date = data.getSerializableExtra("date") as Date?

                date?.also { key ->
                    disposer = MyDataBase.getDatabase(this)
                        .courseAndNameModel()
                        .getItem(key)
                        .take(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Log.d("db", "got item " + it.city)
                            enterLocationDisplayMode(it)
                        }
                }
            } else {
                enterLocationTrackingMode()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_saved_locations -> {
            // User chose the "Settings" item, show the app settings UI...
            val intent = Intent(this, SavedLocationsListActivity::class.java)
            startActivityForResult(intent, SAVEDLOCATION_REQUEST_CODE)
            true
        }
//
//        R.id.action_favorite -> {
//            // User chose the "Favorite" action, mark the current item
//            // as a favorite...
//            true
//        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }
}
