package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.overlay_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import org.springframework.stereotype.Service;

@Service("overlay-check")
public class OverlayCheckService implements QualityService {

    /**
     * Executes the quality check for the given request.
     * Check overlays.
     */
    @Override
    public ChangesetQualityServiceResultDto checkChangesetQuality(ChangesetQualityServiceRequestDto request) {
        System.out.println("OverlayCheckService started");
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
        }
        System.out.println("OverlayCheckService finished");

        return new ChangesetQualityServiceResultDto("overlay-check", true, null);
    }
}
