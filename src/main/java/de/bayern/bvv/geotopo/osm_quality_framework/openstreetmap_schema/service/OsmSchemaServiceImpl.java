package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.service;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper.ObjectTypeEntityMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.repository.ObjectTypeRepository;
import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper.ObjectTypeMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for OpenStreetMap schema.
 */
@Service
@RequiredArgsConstructor
public class OsmSchemaServiceImpl implements OsmSchemaService {

    private final ObjectTypeRepository objectTypeRepository;

    /**
     * Get Object Type Info.
     */
    @Override
    @Transactional
    public ObjectTypeDto getObjectTypeInfo(String objectType) {
        ObjectType obj = this.objectTypeRepository.findByObjectType(objectType)
                .map(ObjectTypeEntityMapper::toDomain)
                .orElse(null);

        return ObjectTypeMapper.toDto(obj);
    }
}
