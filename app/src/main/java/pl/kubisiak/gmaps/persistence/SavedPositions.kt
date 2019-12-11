package pl.kubisiak.gmaps.persistence

import androidx.room.*
import io.reactivex.Observable
import pl.kubisiak.gmaps.SavedPosition
import java.util.*


@Dao
interface SavedPositions {

    @Query("SELECT * FROM SavedPosition")
    fun getItems(): Observable<List<SavedPosition>>?

    @Query("SELECT * FROM SavedPosition WHERE date = :date LIMIT 1")
    fun getItem(date: Date): Observable<SavedPosition>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(modelClass: SavedPosition): Long
}