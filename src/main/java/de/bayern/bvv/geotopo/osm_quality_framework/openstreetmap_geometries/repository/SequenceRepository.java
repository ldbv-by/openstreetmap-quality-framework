package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns the next value of the identifier sequence.
     */
    public long getNextIdentifierSequence() {
        Number value = (Number) entityManager
                .createNativeQuery("SELECT nextval('openstreetmap_geometries.identifier_seq')")
                .getSingleResult();

        return value.longValue();
    }
}
