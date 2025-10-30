package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.registry.ExpressionRegistry;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api.UnifiedDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class ExpressionParser {
    private final ExpressionRegistry registry;
    private final UnifiedDataProvider unifiedDataProvider;

    /**
     * Parse Condition.
     */
    public Expression parse(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode() || node.isEmpty()) return (taggedObject, baseTaggedObject) -> true;

        // ------ Parse operators
        if (node.has("all")) {
            List<Expression> expressions = new ArrayList<>();
            node.get("all").forEach(n -> expressions.add(parse(n)));
            return (taggedObject, baseTaggedObject) -> { for  (Expression expression : expressions) if (!expression.evaluate(taggedObject, baseTaggedObject)) return false; return true; };
        }

        if (node.has("any")) {
            List<Expression> expressions = new ArrayList<>();
            node.get("any").forEach(n -> expressions.add(parse(n)));
            return (taggedObject, baseTaggedObject) -> { for  (Expression expression : expressions) if (expression.evaluate(taggedObject, baseTaggedObject)) return true; return false; };
        }

        if (node.has("not")) {
            Expression expression = parse(node.get("not"));
            return (taggedObject, baseTaggedObject) -> !expression.evaluate(taggedObject, baseTaggedObject);
        }

        // ------ Parse relations
        if (node.has("relations")) {
            JsonNode relationsNode = node.get("relations");
            if (relationsNode.has("conditions") || relationsNode.has("checks")) {
                Expression conditions = parse(relationsNode.path("conditions"));
                Expression checks = parse(relationsNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    for (Relation relation : taggedObject.getRelations()) {
                        if (conditions.evaluate(relation, baseTaggedObject)) {
                            if (!checks.evaluate(relation, baseTaggedObject)) return false;
                        }
                    }

                    return true;
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (relationsNode.isArray()) {
                    relationsNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    expressions.add(parse(relationsNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    for (Relation relation : taggedObject.getRelations()) {
                        for (Expression e : expressions) {
                            if (!e.evaluate(relation, baseTaggedObject)) return false;
                        }
                    }
                    return true;
                };
            }
        }

        // ------ Parse relation members
        if (node.has("relation_members")) {
            JsonNode jsonNode = node.get("relation_members");

            if (jsonNode.has("conditions") || jsonNode.has("checks")) {
                Expression conditions = parse(jsonNode.path("conditions"));
                Expression checks = parse(jsonNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> relationMembers = this.getRelationMembersAsFeature(taggedObject);
                    if (relationMembers.isEmpty()) return false;

                    for (Feature relationMember : relationMembers) {
                        if (conditions.evaluate(relationMember, baseTaggedObject)) {
                            if (!checks.evaluate(relationMember, baseTaggedObject)) return false;
                        }
                    }

                    return true;
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (jsonNode.isArray()) {
                    jsonNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    expressions.add(parse(jsonNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> relationMembers = this.getRelationMembersAsFeature(taggedObject);
                    if (relationMembers.isEmpty()) return false;

                    for (Feature relationMember : relationMembers) {
                        for (Expression e : expressions) {
                            if (!e.evaluate(relationMember, baseTaggedObject)) return false;
                        }
                    }

                    return true;
                };
            }
        }

        // ------ Parse way nodes
        if (node.has("way_nodes")) {
            JsonNode jsonNode = node.get("way_nodes");

            if (jsonNode.has("conditions") || jsonNode.has("checks")) {
                Expression conditions = parse(jsonNode.path("conditions"));
                Expression checks = parse(jsonNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> wayNodes = this.getWayNodesAsFeature(taggedObject);
                    if (wayNodes.isEmpty()) return false;

                    for (Feature wayNode : wayNodes) {
                        if (conditions.evaluate(wayNode, baseTaggedObject)) {
                            if (!checks.evaluate(wayNode, baseTaggedObject)) return false;
                        }
                    }

                    return true;
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (jsonNode.isArray()) {
                    jsonNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    expressions.add(parse(jsonNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> wayNodes = this.getWayNodesAsFeature(taggedObject);
                    if (wayNodes.isEmpty()) return false;

                    for (Feature wayNode : wayNodes) {
                        for (Expression e : expressions) {
                            if (!e.evaluate(wayNode, baseTaggedObject)) return false;
                        }
                    }

                    return true;
                };
            }
        }

        // Parse leafs, e.g. "tag_exists", "tag_regex_match", ...
        return this.registry.fromLeaf(node);
    }

    /**
     * Get way nodes as feature list.
     */
    private List<Feature> getWayNodesAsFeature(TaggedObject taggedObject) {
        List<Feature> wayNodeFeatures = new ArrayList<>();

        if (taggedObject instanceof Feature way) {
            if (way.getGeometryNodes() != null && !way.getGeometryNodes().isEmpty()) {
                Set<Long> osmIds = way.getGeometryNodes().stream().map(GeometryNode::getOsmId).collect(Collectors.toSet());

                DataSet wayNodeTaggedFeatures = Optional.ofNullable(
                                this.unifiedDataProvider.getDataSet(
                                        new DataSetFilter(
                                        null, null, null,
                                        new FeatureFilter(new OsmIds(osmIds, null, null, null), null, null, null))))
                        .map(DataSetMapper::toDomain)
                        .orElse(null);

                for (GeometryNode geometryNode : way.getGeometryNodes()) {
                    Feature wayNodeFeature = null;
                    if (wayNodeTaggedFeatures != null && wayNodeTaggedFeatures.getNodes() != null) {
                        wayNodeFeature = wayNodeTaggedFeatures.getNodes()
                                .stream().filter(n -> n.getOsmId().equals(geometryNode.getOsmId())).findFirst().orElse(null);
                    }

                    if (wayNodeFeature == null) {
                        wayNodeFeature = new Feature(
                                geometryNode.getGeometry(),
                                geometryNode.getGeometryTransformed(),
                                List.of(geometryNode)
                        );

                        wayNodeFeature.setOsmId(geometryNode.getOsmId());
                    }

                    wayNodeFeatures.add(wayNodeFeature);
                }
            }
        }

        return wayNodeFeatures;
    }

    /**
     * Get relation members as feature list.
     */
    private List<Feature> getRelationMembersAsFeature(TaggedObject taggedObject) {
        List<Feature> relationMemberFeatures = new ArrayList<>();

        if (taggedObject instanceof Relation relation) {
            if (relation.getMembers() != null && !relation.getMembers().isEmpty()) {
                DataSet relationMemberTaggedFeatures = Optional.ofNullable(
                                this.unifiedDataProvider.getRelationMembers(relation.getOsmId(), null, null))
                        .map(DataSetMapper::toDomain)
                        .orElse(null);

                if (relationMemberTaggedFeatures != null && relationMemberTaggedFeatures.getAll() != null) {
                    for (TaggedObject relationMemberTaggedObject : relationMemberTaggedFeatures.getAll()) {
                        if (relationMemberTaggedObject instanceof Feature) {
                            relationMemberFeatures.add((Feature) relationMemberTaggedObject);
                        }
                    }
                }
            }
        }

        return relationMemberFeatures;
    }
}
