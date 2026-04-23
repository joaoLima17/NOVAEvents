package pt.unl.fct.iadi.novaevents.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.Client.OpenWeatherClient

@Service
class WeatherService(
    private val openWeatherClient: OpenWeatherClient,
    @Value("\${weather.api.key}") private val apiKey: String
) {
    private val log = LoggerFactory.getLogger(WeatherService::class.java)

    /**
     * Returns true if it is currently raining at the given location,
     * false if not raining, null if the weather data is unavailable.
     */
    fun isRaining(location: String): Boolean? {
        return try {
            val response = openWeatherClient.getWeather(q = location, appid = apiKey, units = "metric")
            response.weather.firstOrNull()?.main?.equals("Rain", ignoreCase = true) ?: false
        } catch (e: Exception) {
            log.warn("Could not fetch weather for location '{}': {}", location, e.message)
            null
        }
    }
}