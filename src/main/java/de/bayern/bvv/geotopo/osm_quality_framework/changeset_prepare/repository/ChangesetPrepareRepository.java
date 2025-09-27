package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import org.springframework.stereotype.Repository;

/**
 * Repository for the changeset_prepare_X schema. (interface)
 */
@Repository
public interface ChangesetPrepareRepository {

    /**
     * Creates a changeset prepare schema.
     */
    void createSchemaByChangesetId(Long changesetId);

    /**
     * Drops a changeset prepare schema.
     */
    void dropSchemaByChangeset(Long changesetId);

    /**
     * Copies all depending changeset objects from openstreetmap_geometries schema.
     */
    void insertDependingOsmObjects(Changeset changeset);
}
