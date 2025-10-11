package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.NodeEntity;
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
public class NodeRepositoryImpl extends CommonRepositoryImpl<NodeEntity> implements NodeRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<NodeEntity> findByFeatureFilter(FeatureFilter featureFilter) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<NodeEntity> criteriaQuery = criteriaBuilder.createQuery(NodeEntity.class);
        Root<NodeEntity> nodeRoot = criteriaQuery.from(NodeEntity.class);

        TypedQuery<NodeEntity> query = this.getTypedQuery(this.entityManager, criteriaQuery, nodeRoot, featureFilter);

        return query.getResultList();
    }
}
