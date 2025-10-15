package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class AreaRepositoryImpl extends CommonRepositoryImpl<AreaEntity> implements AreaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AreaEntity> fetchByFeatureFilter(FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<AreaEntity> criteriaQuery = criteriaBuilder.createQuery(AreaEntity.class);
        Root<AreaEntity> areaRoot = criteriaQuery.from(AreaEntity.class);

        TypedQuery<AreaEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, areaRoot, featureFilter);

        return query.getResultList();
    }
}
