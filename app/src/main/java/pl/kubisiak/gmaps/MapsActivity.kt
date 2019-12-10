package pl.kubisiak.gmaps

import android.Manifest
import android.content.Context
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
import pl.kubisiak.gmaps.owm.OWMService
import pl.kubisiak.gmaps.owm.createOWMService
import pl.kubisiak.gmaps.persistence.MyDataBase
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


private const val PERMISSIONS_REQUEST_CODE = 11

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
            locationSubject.value?.also {
                val xx = LatLng(it.latitude, it.longitude)
                disposer = Completable.create {
                        MyDataBase.getDatabase(this).courseAndNameModel().addData(SavedPosition(xx, Date(), null))
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
        }

        disposer = locationSubject
            .sample(10, TimeUnit.SECONDS)
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
                cityName.text = getString(R.string.main_screen_city, it.name)
                temperature.text = getString(R.string.main_screen_temp, it.main?.temp)
                weather.text = getString(R.string.main_screen_weather, it.weather?.firstOrNull()?.description)
            })
    }

    private val theRealDisposer = CompositeDisposable()
    private var disposer: Disposable
        get() = throw Exception("WTF")
        set(value) {
            theRealDisposer.add(value)
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
            whenLocationAllowed()
        } else {
            requestPermissions()
        }
        // Add a marker in Sydney and move the camera
        val modlin = LatLng(52.45, 20.65)
        mMap.addMarker(MarkerOptions().position(modlin).title("Lotnisko w Modlinie"))
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
                    whenLocationAllowed()
                }
            }
        }
    }

    private fun whenLocationAllowed() {
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        R.id.action_favorite -> {
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
            true
        }

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
