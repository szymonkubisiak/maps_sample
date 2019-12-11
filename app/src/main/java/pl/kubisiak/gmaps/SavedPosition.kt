package pl.kubisiak.gmaps

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class SavedPosition(
    val latitude: Double,
    val longitude: Double,
    @PrimaryKey
    val date: Date,
    val temperature: Double? ) : Parcelable {

    val position: LatLng
        get() = LatLng(latitude, longitude)
}