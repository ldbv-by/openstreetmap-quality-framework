package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ChangesetNodeRepositoryImpl extends ChangesetCommonRepositoryImpl<NodeEntity> implements ChangesetNodeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NodeEntity> fetchByFeatureFilter(Long changesetId, FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<NodeEntity> criteriaQuery = criteriaBuilder.createQuery(NodeEntity.class);
        Root<NodeEntity> nodeRoot = criteriaQuery.from(NodeEntity.class);

        TypedQuery<NodeEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, nodeRoot, featureFilter, changesetId);

        return query.getResultList();
    }
}
