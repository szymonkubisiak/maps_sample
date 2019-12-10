package pl.kubisiak.gmaps.owm

data class WeatherResponse (
      val coord: Coord?
    , val weather: MutableList<WeatherElement>?
    , val main: Main?
    , var name: String?
)

