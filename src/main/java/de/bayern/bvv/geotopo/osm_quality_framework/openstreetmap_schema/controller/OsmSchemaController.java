package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.controller;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that provides the institutional domain schema to external applications.
 */
@RestController
@RequestMapping("/osm-quality-framework/v1/osm-schema")
@RequiredArgsConstructor
@Slf4j
public class OsmSchemaController {

    private final OsmSchemaService osmSchemaService;

    /**
     * Returns the list of object types of the institutional schema.
     */
    @GetMapping
    public ResponseEntity<List<ObjectTypeDto>> getObjectTypes(
            @RequestParam(name = "flat-tags", required = false, defaultValue = "false") boolean flattingTags,
            @RequestParam(name = "with-rules", required = false, defaultValue = "true") boolean withRules
    ) {
        List<ObjectTypeDto> objectTypes = this.osmSchemaService.getObjectTypes(flattingTags, withRules);
        return ResponseEntity.ok(objectTypes);
    }
}
