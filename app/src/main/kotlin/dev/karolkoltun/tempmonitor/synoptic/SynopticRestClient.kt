package dev.karolkoltun.tempmonitor.synoptic

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class SynopticRestClient(
    private val synopticProperties: SynopticProperties,
    restClientBuilder: RestClient.Builder
) {

    private val restClient = restClientBuilder.baseUrl("https://danepubliczne.imgw.pl/").build()

    fun get(): String {
        return this.restClient.get()
            .uri("/api/data/synop/id/" + synopticProperties.stationId)
            .retrieve()
            .body<String>() ?: throw IllegalStateException("No response body received")
    }

}