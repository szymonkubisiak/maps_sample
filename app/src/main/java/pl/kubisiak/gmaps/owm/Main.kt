package pl.kubisiak.gmaps.owm

data class Main (
      var temp: Double? = null
    , var tempMin: Double? = null
    , var tempMax: Double? = null
    , var pressure: Double? = null
    , var seaLevel: Double? = null
    , var grndLevel: Double? = null
    , var humidity: Int? = null
    , var tempKf: Int? = null
)