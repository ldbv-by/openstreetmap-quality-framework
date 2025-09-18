package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.controller;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_contract.dto.ChangesetQualityResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.service.QualityHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to manage quality check requests.
 */
@RestController
@RequestMapping("/osm-quality-framework/v1/quality-hub")
@RequiredArgsConstructor
@Slf4j
public class QualityHubController {

    private final QualityHubService qualityHubService;

    /**
     * Persists the changeset and publishes it to the configured quality services.
     */
    @PostMapping(
            value = "/check/changeset/{changesetId}",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
//    public ResponseEntity<List<ChangesetQualityResultDto>> checkChangesetQuality(
    public ResponseEntity<ChangesetDto> checkChangesetQuality(
            @PathVariable Long changesetId,
            @RequestBody ChangesetDto changesetDto) {

        log.info("Check changeset {}: {}", changesetId, changesetDto);

        List<ChangesetQualityResultDto> changesetQualityResults =
                this.qualityHubService.checkChangesetQuality(changesetId, changesetDto);

        log.info("Result for changeset {}: {}", changesetId, changesetQualityResults);

        //return ResponseEntity.ok(changesetQualityResults);
        return ResponseEntity.ok(changesetDto);
    }
}
