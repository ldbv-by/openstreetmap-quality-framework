package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.repository;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommonRepositoryImpl<T> {

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
            Predicate criteriaPredicate = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, entityType, (From<?, ?>) root, entityType, criteria);
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

    private Predicate criteriaToPredicate(CriteriaQuery<?> criteriaQuery,
                                          CriteriaBuilder criteriaBuilder,
                                          Root<T> root, Class<?> rootEntityType,
                                          From<?, ?> target, Class<?> targetEntityType,
                                          Criteria criteria) {
        switch (criteria) {
            case null -> {
                return criteriaBuilder.conjunction();
            }

            case All(List<Criteria> items) -> {
                List<Predicate> ps = new ArrayList<>();
                if (items != null) {
                    for (Criteria child : items) {
                        Predicate p = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, rootEntityType, target, targetEntityType, child);
                        if (p != null) ps.add(p);
                    }
                }
                return ps.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(ps.toArray(new Predicate[0]));
            }

            case Any(List<Criteria> items) -> {
                List<Predicate> ps = new ArrayList<>();
                if (items != null) {
                    for (Criteria child : items) {
                        Predicate p = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, rootEntityType, target, targetEntityType, child);
                        if (p != null) ps.add(p);
                    }
                }
                return ps.isEmpty() ? criteriaBuilder.disjunction() : criteriaBuilder.or(ps.toArray(new Predicate[0]));
            }

            case Not(Criteria expr) -> {
                Predicate inner = this.criteriaToPredicate(criteriaQuery, criteriaBuilder, root, rootEntityType, target, targetEntityType, expr);
                return (inner == null) ? criteriaBuilder.conjunction() : criteriaBuilder.not(inner);
            }

            case Leaf leaf -> {
                return this.leafToPredicate(criteriaQuery, criteriaBuilder, target, targetEntityType, leaf);
            }

            default -> {}
        }

        return criteriaBuilder.conjunction();
    }

    private Predicate leafToPredicate(CriteriaQuery<?> criteriaQuery,
                                      CriteriaBuilder criteriaBuilder,
                                      From<?, ?> target, Class<?> entityType,
                                      Leaf leaf) {
        String type = leaf.type();
        Map<String, Object> params = leaf.params() == null ? Map.of() : leaf.params();

        Function<String, Expression<String>> tagExpr =
                key -> criteriaBuilder.function("jsonb_extract_path_text", String.class, target.get("tags"), criteriaBuilder.literal(key));

        switch (type) {
            case "tag_equals": {
                String key   = (String) params.get("tag_key");
                String value = (String) params.get("value");
                if (key == null) return criteriaBuilder.conjunction();
                if (value == null) return criteriaBuilder.isNull(tagExpr.apply(key));
                return criteriaBuilder.equal(tagExpr.apply(key), value);
            }

            case "tag_regex_match": {
                String key     = (String) params.get("tag_key");
                String pattern = (String) params.get("pattern");

                if (key == null) {
                    throw new IllegalArgumentException(type + ": 'tag_key' is required.");
                }
                if (pattern == null || pattern.isBlank()) {
                    throw new IllegalArgumentException(type + ": 'pattern' is required.");
                }

                Expression<String> tagValue = tagExpr.apply(key);

                Expression<Object> regexpResult = criteriaBuilder.function(
                        "regexp_match",
                        Object.class,
                        tagValue,
                        criteriaBuilder.literal(pattern)
                );

                Predicate tagNotNull = criteriaBuilder.isNotNull(tagValue);
                Predicate matchNotNull = criteriaBuilder.isNotNull(regexpResult);

                return criteriaBuilder.and(tagNotNull, matchNotNull);
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
                                target.get("tags"), criteriaBuilder.literal(key));

                return criteriaBuilder.isNotNull(jsonPath);
            }

            case "relation_exists": {
                Criteria relationCriteria = this.parseCriteria(params.get("criteria"));

                // EXISTS subquery
                Subquery<Long> relationExistsSub = criteriaQuery.subquery(Long.class);

                Root<RelationEntity>       rel = relationExistsSub.from(RelationEntity.class);
                Root<RelationMemberEntity> rm = relationExistsSub.from(RelationMemberEntity.class);

                List<Predicate> relationExistsPredicates = new ArrayList<>();

                // rel.osm_id = rm.relation_osm_id
                Path<Long> rmRelationOsmId = rm.get("memberId").get("relationOsmId");
                relationExistsPredicates.add(criteriaBuilder.equal(rel.get("osmId"), rmRelationOsmId));

                // rm.member_osm_id = :currentOsmId
                Path<Long> rmMemberOsmId = rm.get("memberId").get("memberOsmId");
                relationExistsPredicates.add(criteriaBuilder.equal(rmMemberOsmId, target.get("osmId")));

                // rm.member_type passend zum Target
                if (NodeEntity.class.isAssignableFrom(entityType)) {
                    relationExistsPredicates.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "n"));
                } else if (WayEntity.class.isAssignableFrom(entityType)) {
                    relationExistsPredicates.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "w"));
                } else if (RelationEntity.class.isAssignableFrom(entityType)) {
                    relationExistsPredicates.add(criteriaBuilder.equal(criteriaBuilder.lower(rm.get("memberId").get("memberType")), "r"));
                } else if (AreaEntity.class.isAssignableFrom(entityType)) {
                    // Area kann aus 'w' oder 'r' stammen
                    relationExistsPredicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(rm.get("memberId").get("memberType")),
                            criteriaBuilder.lower(target.get("osmGeometryType"))));
                }

                // optional: filter relation criteria
                if (relationCriteria != null) {
                    Predicate relCriteriaPred = this.criteriaToPredicate(criteriaQuery, criteriaBuilder,
                            (Root<T>) target, entityType, rel, RelationEntity.class, relationCriteria);

                    if (relCriteriaPred != null) {
                        relationExistsPredicates.add(relCriteriaPred);
                    }
                }

                // optional: filter changeset
                try {
                    Path<Long> targetChangesetId = target.get("changeset").get("id");
                    Path<Long> rmChangesetId   = rm.get("changeset").get("id");
                    relationExistsPredicates.add(criteriaBuilder.equal(rmChangesetId, targetChangesetId));
                    Path<Long> relChangesetId   = rel.get("changeset").get("id");
                    relationExistsPredicates.add(criteriaBuilder.equal(relChangesetId, targetChangesetId));
                } catch (IllegalArgumentException ignored) {}

                // optional: filter relation members
                @SuppressWarnings("unchecked")
                Map<String, Object> relationMemberFilter = (Map<String, Object>) params.get("relation_members");

                String relationMemberRole = null;
                Criteria relationMemberCriteria = null;
                if (relationMemberFilter != null) {
                    relationMemberRole = (String) relationMemberFilter.get("role");
                    relationMemberCriteria = this.parseCriteria(relationMemberFilter.get("criteria"));
                }

                // optional: filter member criteria
                if (relationMemberFilter != null) {
                    Predicate existsNodeMember = buildRelationMemberExists(
                            criteriaQuery, criteriaBuilder, rel,
                            relationMemberRole, relationMemberCriteria,
                            "n", NodeEntity.class, null
                    );

                    Predicate existsWayMember = buildRelationMemberExists(
                            criteriaQuery, criteriaBuilder, rel,
                            relationMemberRole, relationMemberCriteria,
                            "w", WayEntity.class, null
                    );

                    // Area: kann aus w/r stammen -> zwei Exists (oder eins mit extra predicate)
                    Predicate existsAreaWMember = buildRelationMemberExists(
                            criteriaQuery, criteriaBuilder, rel,
                            relationMemberRole, relationMemberCriteria,
                            "w", AreaEntity.class,
                            (cb, memberRoot) -> cb.equal(cb.lower(memberRoot.get("osmGeometryType")), "w")
                    );

                    Predicate existsAreaRMember = buildRelationMemberExists(
                            criteriaQuery, criteriaBuilder, rel,
                            relationMemberRole, relationMemberCriteria,
                            "r", AreaEntity.class,
                            (cb, memberRoot) -> cb.equal(cb.lower(memberRoot.get("osmGeometryType")), "r")
                    );

                    Predicate existsRelationMember = buildRelationMemberExists(
                            criteriaQuery, criteriaBuilder, rel,
                            relationMemberRole, relationMemberCriteria,
                            "r", RelationEntity.class, null
                    );

                    relationExistsPredicates.add(
                            criteriaBuilder.or(
                                    existsNodeMember,
                                    existsWayMember,
                                    existsAreaWMember,
                                    existsAreaRMember,
                                    existsRelationMember
                            )
                    );
                }

                relationExistsSub.select(rel.get("osmId")).where(relationExistsPredicates.toArray(new Predicate[0]));
                return criteriaBuilder.exists(relationExistsSub);
            }

            case "bbox": {
                Double minX = (Double) params.get("min_x");
                Double minY = (Double) params.get("min_y");
                Double maxX = (Double) params.get("max_x");
                Double maxY = (Double) params.get("max_y");
                int srid = params.containsKey("srid") ? ((Number) params.get("srid")).intValue() : 4326;

                if (minX == null || minY == null || maxX == null || maxY == null || RelationEntity.class.isAssignableFrom(entityType)) return criteriaBuilder.conjunction();

                Path<Geometry> geomPath;
                try {
                    geomPath = target.get("geom");
                } catch (IllegalArgumentException e) {
                    return criteriaBuilder.conjunction();
                }

                Expression<Geometry> bboxEnvelope = criteriaBuilder.function("ST_MakeEnvelope", Geometry.class,
                        criteriaBuilder.literal(minX),
                        criteriaBuilder.literal(minY),
                        criteriaBuilder.literal(maxX),
                        criteriaBuilder.literal(maxY),
                        criteriaBuilder.literal(srid));

                Expression<Geometry> geomEnvelope = criteriaBuilder.function("ST_Envelope", Geometry.class, geomPath);
                Expression<Boolean> bboxOverlap = criteriaBuilder.function("ST_Intersects", Boolean.class,
                        geomEnvelope, bboxEnvelope);

                Expression<Boolean> within    = criteriaBuilder.function("ST_Within",    Boolean.class, geomPath, bboxEnvelope);
                Expression<Boolean> contains  = criteriaBuilder.function("ST_Contains",  Boolean.class, geomPath, bboxEnvelope);
                Expression<Boolean> intersects= criteriaBuilder.function("ST_Intersects",Boolean.class, geomPath, bboxEnvelope);

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

    @SuppressWarnings("unchecked")
    private Criteria parseCriteria(Object raw) {
        if (raw == null) return null;
        if (raw instanceof Criteria c) return c;

        if (raw instanceof Map<?, ?> mapRaw) {
            Map<String, Object> m = (Map<String, Object>) mapRaw;

            if (m.containsKey("all")) {
                List<Object> arr = (List<Object>) m.get("all");
                List<Criteria> items = new ArrayList<>();
                if (arr != null) for (Object o : arr) {
                    Criteria c = parseCriteria(o);
                    if (c != null) items.add(c);
                }
                return new All(items);
            }

            if (m.containsKey("any")) {
                List<Object> arr = (List<Object>) m.get("any");
                List<Criteria> items = new ArrayList<>();
                if (arr != null) for (Object o : arr) {
                    Criteria c = parseCriteria(o);
                    if (c != null) items.add(c);
                }
                return new Any(items);
            }

            if (m.containsKey("not")) {
                return new Not(parseCriteria(m.get("not")));
            }

            Object typeObj = m.get("type");
            if (typeObj instanceof String type && !type.isBlank()) {
                Object paramsObj = m.get("params");
                Map<String, Object> params;
                if (paramsObj instanceof Map<?, ?> p) {
                    params = (Map<String, Object>) p;
                } else {
                    params = new java.util.LinkedHashMap<>(m);
                    params.remove("type");
                }
                return new Leaf(type, params);
            }
        }

        return null;
    }

    @FunctionalInterface
    private interface ExtraMemberEntityPredicate {
        Predicate apply(CriteriaBuilder cb, Root<?> memberRoot);
    }

    /**
     * EXISTS: Für eine gegebene Relation (rel) existiert ein RelationMember rm2,
     * das (optional) die Rolle erfüllt und dessen Member-Entity (Node/Way/Area/Relation)
     * die relationMemberCriteria erfüllt.
     */
    private Predicate buildRelationMemberExists(
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            Root<RelationEntity> rel,
            String relationMemberRole,
            Criteria relationMemberCriteria,
            String memberTypeLower,              // "n"|"w"|"r"
            Class<?> memberEntityClass,
            ExtraMemberEntityPredicate extraPredicate
    ) {
        Subquery<Long> sub = criteriaQuery.subquery(Long.class);

        // rm2 ist das Member, das gefiltert werden soll
        Root<RelationMemberEntity> rm2 = sub.from(RelationMemberEntity.class);
        Root<?> memberRoot = sub.from(memberEntityClass);

        List<Predicate> ps = new ArrayList<>();

        // rm2 gehört zur selben Relation
        ps.add(cb.equal(rm2.get("memberId").get("relationOsmId"), rel.get("osmId")));

        // rm2.member_type = memberTypeLower
        ps.add(cb.equal(cb.lower(rm2.get("memberId").get("memberType")), memberTypeLower));

        // memberRoot.osmId = rm2.member_osm_id
        Path<Long> rm2MemberOsmId = rm2.get("memberId").get("memberOsmId");
        ps.add(cb.equal(memberRoot.get("osmId"), rm2MemberOsmId));

        // optional: role filter (auf rm2!)
        if (relationMemberRole != null && !relationMemberRole.isBlank()) {
            ps.add(cb.equal(cb.lower(rm2.get("memberId").get("memberRole")), relationMemberRole.toLowerCase()));
        }

        // optional: changeset koppeln (falls vorhanden)
        try {
            Path<Long> relChangesetId = rel.get("changeset").get("id");
            ps.add(cb.equal(rm2.get("changeset").get("id"), relChangesetId));
            ps.add(cb.equal(memberRoot.get("changeset").get("id"), relChangesetId));
        } catch (IllegalArgumentException ignored) {}

        // optional: criteria auf dem Member-Entity
        if (relationMemberCriteria != null) {
            Predicate memberCriteriaPred = this.criteriaToPredicate(
                    criteriaQuery, cb,
                    null, Object.class,                 // root/rootEntityType werden nicht gebraucht
                    (From<?, ?>) memberRoot, memberEntityClass,
                    relationMemberCriteria
            );
            if (memberCriteriaPred != null) ps.add(memberCriteriaPred);
        }

        // optional: extra predicate (Area: osmGeometryType match)
        if (extraPredicate != null) {
            ps.add(extraPredicate.apply(cb, memberRoot));
        }

        sub.select(cb.literal(1L)).where(ps.toArray(new Predicate[0]));
        return cb.exists(sub);
    }
}
