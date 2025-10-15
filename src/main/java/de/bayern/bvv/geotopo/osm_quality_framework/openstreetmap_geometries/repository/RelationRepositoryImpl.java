package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class RelationRepositoryImpl extends CommonRepositoryImpl<RelationEntity> implements RelationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RelationEntity> fetchByFeatureFilter(FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<RelationEntity> criteriaQuery = criteriaBuilder.createQuery(RelationEntity.class);
        Root<RelationEntity> relationRoot = criteriaQuery.from(RelationEntity.class);

        TypedQuery<RelationEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, relationRoot, featureFilter);

        return query.getResultList();
    }
}
