package pt.unl.fct.iadi.novaevents.Client

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import pt.unl.fct.iadi.novaevents.controller.dto.WeatherResponse

@HttpExchange
interface OpenWeatherClient {
    @GetExchange("/data/2.5/weather")
    fun getWeather(
        @RequestParam q: String,
        @RequestParam appid: String,
        @RequestParam units: String
    ): WeatherResponse
}

@Configuration
class OpenWeatherClientConfig(
    private val builder: RestClient.Builder
) {
    private val log = LoggerFactory.getLogger(OpenWeatherClientConfig::class.java)
    @Bean
    fun openWeatherClient(builder: RestClient.Builder): OpenWeatherClient {
        val restClient = builder
            .baseUrl("https://api.openweathermap.org")
            .requestInterceptor { request, body, execution ->
                val principal = SecurityContextHolder.getContext().authentication?.name
                    ?: "anonymous"
                val response = execution.execute(request, body)
                log.info("[{}] [External API] {} {} [{}]", principal,
                    request.method, request.uri, response.statusCode)
                response
            }
            .build()
        val factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
        return factory.createClient(OpenWeatherClient::class.java)
    }
}