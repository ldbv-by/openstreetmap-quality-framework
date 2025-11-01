package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.WayEntity;
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

public class ChangesetWayRepositoryImpl extends ChangesetCommonRepositoryImpl<WayEntity> implements ChangesetWayRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WayEntity> fetchByFeatureFilter(Long changesetId, OsmIds osmIds, Criteria criteria) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<WayEntity> criteriaQuery = criteriaBuilder.createQuery(WayEntity.class);
        Root<WayEntity> wayRoot = criteriaQuery.from(WayEntity.class);

        TypedQuery<WayEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, wayRoot, osmIds, criteria, changesetId);

        return query.getResultList();
    }
}
