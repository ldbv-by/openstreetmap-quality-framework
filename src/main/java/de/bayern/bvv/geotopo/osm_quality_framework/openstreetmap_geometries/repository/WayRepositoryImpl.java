package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WayRepositoryImpl extends CommonRepositoryImpl<WayEntity> implements WayRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<WayEntity> findByFeatureFilter(FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<WayEntity> criteriaQuery = criteriaBuilder.createQuery(WayEntity.class);
        Root<WayEntity> wayRoot = criteriaQuery.from(WayEntity.class);

        TypedQuery<WayEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, wayRoot, featureFilter);

        return query.getResultList();
    }
}
