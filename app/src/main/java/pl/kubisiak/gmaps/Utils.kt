package pl.kubisiak.gmaps

import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object{
        //var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
        private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        @JvmStatic
        fun formatDate(arg: Date) = simpleDateFormat.format(arg)
    }
}