package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper.ObjectTypeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.repository.ObjectTypeRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to the institutional target schema definitions
 * (e.g. GeoInfoDok of the AdV) that describe how OSM object types
 * must be interpreted and represented in the OSM database.
 * <p>
 * The retrieved schema metadata includes semantic classification rules
 * required by the Rule Engine for quality validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OsmSchemaServiceImpl implements OsmSchemaService {

    private final ObjectTypeRepository objectTypeRepository;

    /**
     * Loads the institutional schema definition for the specified OSM object type.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "cacheObjectTypeInfo", key="#objectType", unless = "#result == null")
    public ObjectTypeDto getObjectTypeInfo(String objectType) {
        long loadStartTime = System.currentTimeMillis();
        ObjectType obj = this.objectTypeRepository.findByObjectType(objectType)
                .map(ObjectTypeEntityMapper::toDomain)
                .orElse(null);

        log.info("getObjectTypeInfo(): object-type={}, checkSchemaTime={} ms",
                objectType, System.currentTimeMillis() - loadStartTime);

        return ObjectTypeMapper.toDto(obj);
    }
}
