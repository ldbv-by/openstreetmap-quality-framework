package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ChangesetAreaRepositoryImpl extends ChangesetCommonRepositoryImpl<AreaEntity> implements ChangesetAreaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AreaEntity> fetchByFeatureFilter(Long changesetId, FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<AreaEntity> criteriaQuery = criteriaBuilder.createQuery(AreaEntity.class);
        Root<AreaEntity> areaRoot = criteriaQuery.from(AreaEntity.class);

        TypedQuery<AreaEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, areaRoot, featureFilter, changesetId);

        return query.getResultList();
    }
}
