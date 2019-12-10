package pl.kubisiak.gmaps.owm

data class WeatherElement (
    var id: Int? = null,
    var main: String? = null,
    var description: String? = null,
    var icon: String? = null
)