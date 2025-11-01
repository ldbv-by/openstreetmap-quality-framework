package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Criteria;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OsmIds;

import java.util.List;

public interface ChangesetNodeRepositoryCustom {
    List<NodeEntity> fetchByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria);
}
