package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.AreaEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.NodeEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.RelationEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.WayEntity;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChangesetCommonRepositoryImpl<T> {

    public TypedQuery<T> getTypedQuery(EntityManager entityManager, CriteriaQuery<T> criteriaQuery, Root<T> root, FeatureFilter featureFilter) {
        return this.getTypedQuery(entityManager, criteriaQuery, root, featureFilter, null);
    }

    public TypedQuery<T> getTypedQuery(EntityManager entityManager, CriteriaQuery<T> criteriaQuery, Root<T> root, FeatureFilter featureFilter, Long changesetId) {
        Class<?> entityType = root.getJavaType();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        // Set filter osm ids.
        if (featureFilter != null && featureFilter.osmIds() != null) {
            if (NodeEntity.class.isAssignableFrom(entityType) && featureFilter.osmIds().nodeIds() != null) {
                predicates.add(root.get("osmId").in(featureFilter.osmIds().nodeIds()));
            } else if (WayEntity.class.isAssignableFrom(entityType) && featureFilter.osmIds().wayIds() != null) {
                predicates.add(root.get("osmId").in(featureFilter.osmIds().wayIds()));
            } else if (AreaEntity.class.isAssignableFrom(entityType) && featureFilter.osmIds().areaIds() != null) {
                predicates.add(root.get("osmId").in(featureFilter.osmIds().areaIds()));
            } else if (RelationEntity.class.isAssignableFrom(entityType) && featureFilter.osmIds().relationIds() != null) {
                predicates.add(root.get("osmId").in(featureFilter.osmIds().relationIds()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("osmId"), -1L));
            }
        }

        // Set filter tags.
        if (featureFilter != null && featureFilter.tags() != null) {
            for (Map.Entry<String, String> tag : featureFilter.tags().entrySet()) {
                if (tag.getValue().contains("|")) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(
                            criteriaBuilder.function("jsonb_extract_path_text",
                                    String.class, root.get("tags"), criteriaBuilder.literal(tag.getKey())));

                    String[] tagValues = tag.getValue().split("\\|");
                    for (String tagValue : tagValues) {
                        if (!tagValue.trim().isEmpty()) {
                            in.value(tagValue.trim());
                        }
                    }

                    predicates.add(in);
                } else {
                    predicates.add(
                            criteriaBuilder.equal(
                                    criteriaBuilder.function("jsonb_extract_path_text",
                                            String.class, root.get("tags"), criteriaBuilder.literal(tag.getKey())),
                                    tag.getValue()
                            ));
                }
            }
        }

        // Set filter bounding box.
        if (featureFilter != null && featureFilter.boundingBox() != null && !RelationEntity.class.isAssignableFrom(entityType)) {
            Expression<Geometry> bboxEnvelope= criteriaBuilder.function("ST_MakeEnvelope", Geometry.class,
                    criteriaBuilder.literal(featureFilter.boundingBox().minX()),
                    criteriaBuilder.literal(featureFilter.boundingBox().minY()),
                    criteriaBuilder.literal(featureFilter.boundingBox().maxX()),
                    criteriaBuilder.literal(featureFilter.boundingBox().maxY()),
                    criteriaBuilder.literal(4326));

            Expression<Geometry> geomEnvelope = criteriaBuilder.function("ST_Envelope", Geometry.class, root.get("geom"));
            Expression<Boolean> bboxOverlap = criteriaBuilder.function("ST_Intersects", Boolean.class,
                    geomEnvelope, bboxEnvelope);

            Expression<Boolean> within     = criteriaBuilder.function("ST_Within",    Boolean.class, root.get("geom"), bboxEnvelope);
            Expression<Boolean> contains   = criteriaBuilder.function("ST_Contains",  Boolean.class, root.get("geom"), bboxEnvelope);
            Expression<Boolean> intersects = criteriaBuilder.function("ST_Intersects",Boolean.class, root.get("geom"), bboxEnvelope);

            Predicate preciseSpatial = criteriaBuilder.or(
                    criteriaBuilder.isTrue(within),
                    criteriaBuilder.isTrue(contains),
                    criteriaBuilder.isTrue(intersects)
            );

            Predicate bboxAndPrecise = criteriaBuilder.and(
                    criteriaBuilder.isTrue(bboxOverlap),
                    preciseSpatial
            );

            predicates.add(bboxAndPrecise);
        }

        // Set filter changeset id.
        if (changesetId != null && changesetId > 0) {
            predicates.add(criteriaBuilder.equal(root.get("changesetId"), changesetId));
        }

        // Combine predicates with AND logic
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery);
    }
}
