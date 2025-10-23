package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.component;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client to call OSM API.
 */
@Component
public class OsmApiClient {

    private final WebClient webClient;

    public OsmApiClient(WebClient.Builder webClientBuilder,
                        @Value("${openstreetmap-api}") String osmApiBaseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(osmApiBaseUrl)
                .build();
    }

    /**
     * Get changeset from the OSM-API.
     */
    public ChangesetDto getChangesetById(Long changesetId) {
        return this.webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/0.6/changeset/{changesetId}/download")
                        .build(changesetId))
                .retrieve()
                .bodyToMono(ChangesetDto.class)
                .block();
    }

}
