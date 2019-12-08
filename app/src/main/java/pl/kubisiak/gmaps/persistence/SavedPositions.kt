package pl.kubisiak.gmaps.persistence

import androidx.room.*
import io.reactivex.Observable
import pl.kubisiak.gmaps.SavedPosition


@Dao
interface SavedPositions {
//    @get:Query("SELECT * FROM SavedPositionsTable")
//    val itemList: Observable<List<SavedPosition>>?

    @Query("SELECT * FROM SavedPosition")
    fun getItems(): Observable<List<SavedPosition>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(modelClass: SavedPosition): Long
}