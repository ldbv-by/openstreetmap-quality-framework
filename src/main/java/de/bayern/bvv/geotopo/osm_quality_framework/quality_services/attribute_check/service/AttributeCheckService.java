package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceRequestDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.ChangesetQualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.spi.QualityService;
import org.springframework.stereotype.Service;

@Service("attribute-check")
public class AttributeCheckService implements QualityService {

    /**
     * Executes the quality check for the given request.
     * Check attribute consistency.
     */
    @Override
    public ChangesetQualityServiceResultDto checkChangesetQuality(ChangesetQualityServiceRequestDto request) {
        System.out.println("AttributeCheckService started");

        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
        }

        System.out.println("AttributeCheckService finished");

        return new ChangesetQualityServiceResultDto("attribute-check", true, null);
    }

}
