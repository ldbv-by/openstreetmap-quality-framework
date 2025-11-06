package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_data.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChangesetCommonRepositoryImpl<T> {

    public TypedQuery<T> getTypedQuery(EntityManager entityManager, CriteriaQuery<T> criteriaQuery, Root<T> root, OsmIds osmIds, Criteria criteria) {
        return this.getTypedQuery(entityManager, criteriaQuery, root, osmIds, criteria, null);
    }

    public TypedQuery<T> getTypedQuery(EntityManager entityManager, CriteriaQuery<T> criteriaQuery, Root<T> root,  OsmIds osmIds, Criteria criteria, Long changesetId) {
        Class<?> entityType = root.getJavaType();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<Predicate> predicates = new ArrayList<>();

        // Set filter osm ids.
        if (osmIds != null) {
            if (NodeEntity.class.isAssignableFrom(entityType) && osmIds.nodeIds() != null) {
                predicates.add(root.get("osmId").in(osmIds.nodeIds()));
            } else if (WayEntity.class.isAssignableFrom(entityType) && osmIds.wayIds() != null) {
                predicates.add(root.get("osmId").in(osmIds.wayIds()));
            } else if (AreaEntity.class.isAssignableFrom(entityType) && osmIds.areaIds() != null) {
                predicates.add(root.get("osmId").in(osmIds.areaIds()));
            } else if (RelationEntity.class.isAssignableFrom(entityType) && osmIds.relationIds() != null) {
                predicates.add(root.get("osmId").in(osmIds.relationIds()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("osmId"), -1L));
            }
        }

        // Set filter criteria.
        if (criteria != null) {
            Predicate criteriaPredicate = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, entityType, criteria);
            if (criteriaPredicate != null) {
                predicates.add(criteriaPredicate);
            }
        }

        // Set filter changeset id.
        if (changesetId != null && changesetId > 0) {
            predicates.add(criteriaBuilder.equal(root.get("changeset").get("id"), changesetId));
        }

        // Combine predicates with AND logic
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(criteriaQuery);
    }

    private Predicate criteriaToPredicate(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<T> root, Class<?> entityType, Criteria criteria) {
        switch (criteria) {
            case null -> {
                return criteriaBuilder.conjunction();
            }

            case All(List<Criteria> items) -> {
                List<Predicate> ps = new ArrayList<>();
                if (items != null) {
                    for (Criteria child : items) {
                        Predicate p = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, entityType, child);
                        if (p != null) ps.add(p);
                    }
                }
                return ps.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(ps.toArray(new Predicate[0]));
            }

            case Any(List<Criteria> items) -> {
                List<Predicate> ps = new ArrayList<>();
                if (items != null) {
                    for (Criteria child : items) {
                        Predicate p = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, entityType, child);
                        if (p != null) ps.add(p);
                    }
                }
                return ps.isEmpty() ? criteriaBuilder.disjunction() : criteriaBuilder.or(ps.toArray(new Predicate[0]));
            }

            case Not(Criteria expr) -> {
                Predicate inner = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, entityType, expr);
                return (inner == null) ? criteriaBuilder.conjunction() : criteriaBuilder.not(inner);
            }

            case Leaf leaf -> {
                return this.leafToPredicate(criteriaQuery, criteriaBuilder, root, entityType, leaf);
            }

            default -> {}
        }

        return criteriaBuilder.conjunction();
    }

    private Predicate leafToPredicate(CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, Root<T> root, Class<?> entityType, Leaf leaf) {
        String type = leaf.type();
        Map<String, Object> params = leaf.params() == null ? Map.of() : leaf.params();

        Function<String, Expression<String>> tagExpr =
                key -> criteriaBuilder.function("jsonb_extract_path_text", String.class, root.get("tags"), criteriaBuilder.literal(key));

        switch (type) {
            case "tag_equals": {
                String key   = (String) params.get("tag_key");
                String value = (String) params.get("value");
                if (key == null) return criteriaBuilder.conjunction();
                if (value == null) return criteriaBuilder.isNull(tagExpr.apply(key));
                return criteriaBuilder.equal(tagExpr.apply(key), value);
            }

            case "tag_in": {
                String key = (String) params.get("tag_key");
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) params.get("values");

                if (key == null) {
                    throw new IllegalArgumentException(type + ": 'tag_key' is required.");
                }

                if (values == null || values.isEmpty()) {
                    throw new IllegalArgumentException(type + ": 'values' is required.");
                }

                Expression<String> tagValue = tagExpr.apply(key);
                List<Predicate> valuePredicates = new ArrayList<>();
                for (String raw : values) {
                    if (raw == null) continue;
                    String value = raw.trim();
                    if (value.isEmpty()) continue;

                    Predicate exact = criteriaBuilder.equal(tagValue, value);
                    Predicate atStart = criteriaBuilder.like(tagValue, value + ";%");
                    Predicate inMiddle = criteriaBuilder.like(tagValue, "%;" + value + ";%");
                    Predicate atEnd = criteriaBuilder.like(tagValue, "%;" + value);
                    valuePredicates.add(criteriaBuilder.or(exact, atStart, inMiddle, atEnd));
                }

                return criteriaBuilder.and(
                        criteriaBuilder.or(valuePredicates.toArray(new Predicate[0]))
                );
            }

            case "tag_exists": {
                String key = (String) params.get("tag_key");

                if (key == null) {
                    throw new IllegalArgumentException(type + ": 'tag_key' is required.");
                }

                Expression<Object> jsonPath =
                        criteriaBuilder.function("jsonb_extract_path", Object.class,
                                root.get("tags"), criteriaBuilder.literal(key));

                return criteriaBuilder.isNotNull(jsonPath);
            }

            case "relation_exists": {
                String relationObjectType = ((String) params.get("object_type"));
                @SuppressWarnings("unchecked")
                Map<String, Object> memberFilter = (Map<String, Object>) params.get("member_filter");

                String memberRole = null;
                String memberObjectType = null;
                if (memberFilter != null) {
                    memberRole = (String) memberFilter.get("role");
                    memberObjectType = (String) memberFilter.get("object_type");
                }

                if (relationObjectType == null || relationObjectType.isBlank()) {
                    throw new IllegalArgumentException("relation_exists: 'object_type' (for relation) is required.");
                }

                // EXISTS subquery
                Subquery<Long> sub = criteriaQuery.subquery(Long.class);

                Root<RelationMemberEntity> rm = sub.from(RelationMemberEntity.class);
                Root<RelationEntity>       rel = sub.from(RelationEntity.class);

                // Kandidaten-Roots für das Mitglied
                Root<NodeEntity>     n  = sub.from(NodeEntity.class);
                Root<WayEntity>      w  = sub.from(WayEntity.class);
                Root<AreaEntity>     a  = sub.from(AreaEntity.class);
                Root<RelationEntity> rr = sub.from(RelationEntity.class);

                List<Predicate> subPs = new ArrayList<>();

                // r.osm_id = rm.relation_osm_id
                Path<Long> rmRelationOsmId = rm.get("memberId").get("relationOsmId");
                subPs.add(criteriaBuilder.equal(rel.get("osmId"), rmRelationOsmId));

                // r.object_type = :relationObjectType
                subPs.add(criteriaBuilder.equal(criteriaBuilder.lower(rel.get("objectType")),
                        relationObjectType.toLowerCase()));

                // rm.member_osm_id = :currentOsmId
                Path<Long> rmMemberOsmId = rm.get("memberId").get("memberOsmId");
                subPs.add(criteriaBuilder.equal(rmMemberOsmId, root.get("osmId")));

                // rm.member_type passend zum Root (wie bisher)
                if (NodeEntity.class.isAssignableFrom(entityType)) {
                    subPs.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "n"));
                } else if (WayEntity.class.isAssignableFrom(entityType)) {
                    subPs.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "w"));
                } else if (RelationEntity.class.isAssignableFrom(entityType)) {
                    subPs.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "r"));
                } else if (AreaEntity.class.isAssignableFrom(entityType)) {
                    // Area kann aus 'w' oder 'r' stammen → wie bei dir:
                    subPs.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(rm.get("memberId").get("memberType")),
                            criteriaBuilder.lower(root.get("osmGeometryType"))));
                }

                // optional: gleiches Changeset
                try {
                    Path<Long> rootChangesetId = root.get("changeset").get("id");
                    Path<Long> rmChangesetId   = rm.get("changeset").get("id");
                    subPs.add(criteriaBuilder.equal(rmChangesetId, rootChangesetId));
                } catch (IllegalArgumentException ignored) {}

                // optional: Rollenfilter
                if (memberRole != null && !memberRole.isBlank()) {
                    Path<String> rmRole = rm.get("memberId").get("memberRole");
                    subPs.add(criteriaBuilder.equal(criteriaBuilder.lower(rmRole), memberRole.toLowerCase()));
                }

                // --- Mitglieds-ObjectType-Filter ---
                // Falls kein member_object_type gefordert ist, brauchen wir keinen Join-OR-Block.
                if (memberObjectType != null && !memberObjectType.isBlank()) {
                    Expression<String> rmType = criteriaBuilder.lower(rm.get("memberId").get("memberType"));
                    String memberObjTypeLc = memberObjectType.toLowerCase();

                    Predicate nodeBranch = criteriaBuilder.and(
                            criteriaBuilder.equal(rmType, "n"),
                            criteriaBuilder.equal(n.get("osmId"), rmMemberOsmId),
                            criteriaBuilder.equal(criteriaBuilder.lower(n.get("objectType")), memberObjTypeLc)
                    );

                    Predicate wayBranch = criteriaBuilder.and(
                            criteriaBuilder.equal(rmType, "w"),
                            criteriaBuilder.equal(w.get("osmId"), rmMemberOsmId),
                            criteriaBuilder.equal(criteriaBuilder.lower(w.get("objectType")), memberObjTypeLc)
                    );

                    // Area kann 'w' oder 'r' als source-geom haben → matchen über osmGeometryType = rm.memberType
                    Predicate areaBranch = criteriaBuilder.and(
                            criteriaBuilder.or(
                                    criteriaBuilder.equal(rmType, "w"),
                                    criteriaBuilder.equal(rmType, "r")
                            ),
                            criteriaBuilder.equal(a.get("osmId"), rmMemberOsmId),
                            criteriaBuilder.equal(
                                    criteriaBuilder.lower(a.get("osmGeometryType")), rmType
                            ),
                            criteriaBuilder.equal(criteriaBuilder.lower(a.get("objectType")), memberObjTypeLc)
                    );

                    Predicate relBranch = criteriaBuilder.and(
                            criteriaBuilder.equal(rmType, "r"),
                            criteriaBuilder.equal(rr.get("osmId"), rmMemberOsmId),
                            criteriaBuilder.equal(criteriaBuilder.lower(rr.get("objectType")), memberObjTypeLc)
                    );

                    // mind. ein Branch muss passen
                    subPs.add(criteriaBuilder.or(nodeBranch, wayBranch, areaBranch, relBranch));
                }

                sub.select(rel.get("osmId")).where(subPs.toArray(new Predicate[0]));
                return criteriaBuilder.exists(sub);
            }

            case "bbox": {
                Double minX = (Double) params.get("min_x");
                Double minY = (Double) params.get("min_y");
                Double maxX = (Double) params.get("max_x");
                Double maxY = (Double) params.get("max_y");
                int srid = params.containsKey("srid") ? ((Number) params.get("srid")).intValue() : 4326;

                if (minX == null || minY == null || maxX == null || maxY == null || RelationEntity.class.isAssignableFrom(entityType)) return criteriaBuilder.conjunction();

                Expression<Geometry> bboxEnvelope = criteriaBuilder.function("ST_MakeEnvelope", Geometry.class,
                        criteriaBuilder.literal(minX),
                        criteriaBuilder.literal(minY),
                        criteriaBuilder.literal(maxX),
                        criteriaBuilder.literal(maxY),
                        criteriaBuilder.literal(srid));

                Expression<Geometry> geomEnvelope = criteriaBuilder.function("ST_Envelope", Geometry.class, root.get("geom"));
                Expression<Boolean> bboxOverlap = criteriaBuilder.function("ST_Intersects", Boolean.class,
                        geomEnvelope, bboxEnvelope);

                Expression<Boolean> within    = criteriaBuilder.function("ST_Within",    Boolean.class, root.get("geom"), bboxEnvelope);
                Expression<Boolean> contains  = criteriaBuilder.function("ST_Contains",  Boolean.class, root.get("geom"), bboxEnvelope);
                Expression<Boolean> intersects= criteriaBuilder.function("ST_Intersects",Boolean.class, root.get("geom"), bboxEnvelope);

                Predicate preciseSpatial = criteriaBuilder.or(
                        criteriaBuilder.isTrue(within),
                        criteriaBuilder.isTrue(contains),
                        criteriaBuilder.isTrue(intersects)
                );

                return criteriaBuilder.and(criteriaBuilder.isTrue(bboxOverlap), preciseSpatial);
            }

            default:
                throw new IllegalArgumentException("Invalid leaf type: " + leaf.type());
        }
    }
}
