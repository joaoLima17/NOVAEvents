package pt.unl.fct.iadi.novaevents.controller.dto


data class WeatherCondition(val main: String, val description: String)
data class WeatherResponse(val name: String, val weather: List<WeatherCondition>)