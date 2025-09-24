package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangesetPrepareRepository {
    void createSchemaByChangesetId(Long changesetId);
    void dropSchemaByChangeset(Long changesetId);
    void insertDependingOsmObjects(Changeset changeset);
}
