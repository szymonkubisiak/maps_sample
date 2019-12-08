package pl.kubisiak.gmaps.persistence

import android.content.Context
import androidx.room.*
import pl.kubisiak.gmaps.SavedPosition
import java.util.*


@Database(entities = arrayOf(SavedPosition::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDataBase : RoomDatabase() {
    abstract fun courseAndNameModel() : SavedPositions

    companion object {
        private var instance: MyDataBase? = null

        fun getDatabase(context: Context) : MyDataBase
        {
            if (instance == null) {
                instance = Room.databaseBuilder(context, MyDataBase::class.java, "your_db_name").build()
            }
            return instance!!
        }

    }
}


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
