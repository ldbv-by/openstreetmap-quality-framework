package de.bayern.bvv.geotopo.osm_quality_framework.changeset_management.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model.Changeset;
import org.springframework.stereotype.Repository;

/**
 * Repository for the changeset_data schema. (interface)
 */
@Repository
public interface ChangesetDataRepository {

    /**
     * Deletes all osm objects for the given changeset id in the osm data tables.
     */
    void deleteChangesetData(Long changesetId);

    /**
     * Copies prepared osm objects from the changeset_prepare schema.
     */
    void copyPreparedData(Long changesetId);

    /**
     * Init changeset.
     */
    void initChangeset(Changeset changeset);

    /**
     * Inserts all changeset objects with operation type (CREATE, MODIFY, DELETE).
     */
    void insertChangesetObjects(Changeset changeset);
}
