package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Criteria;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OsmIds;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class ChangesetRelationRepositoryImpl extends ChangesetCommonRepositoryImpl<RelationEntity> implements ChangesetRelationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RelationEntity> fetchByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<RelationEntity> criteriaQuery = criteriaBuilder.createQuery(RelationEntity.class);
        Root<RelationEntity> relationRoot = criteriaQuery.from(RelationEntity.class);

        TypedQuery<RelationEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, relationRoot, osmIds, criteria, changesetId);

        return query.getResultList();
    }
}
