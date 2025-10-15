package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;

import java.util.List;

public interface ChangesetNodeRepositoryCustom {
    List<NodeEntity> fetchByFeatureFilter(Long changesetId, FeatureFilter featureFilter);
}
