package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.mapper.DataSetMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.registry.ExpressionRegistry;
import de.bayern.bvv.geotopo.osm_quality_framework.geodata_view.api.GeodataViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public final class ExpressionParser {
    private final ExpressionRegistry registry;
    private final GeodataViewService geodataViewService;

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
            LoopInfo loopInfo = this.parseLoopInfo(relationsNode);

            if (relationsNode.has("conditions") || relationsNode.has("checks")) {
                Expression conditions = parse(relationsNode.path("conditions"));
                Expression checks = parse(relationsNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    int candidates = 0;
                    int success    = 0;

                    for (Relation relation : taggedObject.getRelations()) {
                        if (conditions.evaluate(relation, baseTaggedObject)) {
                            candidates++;
                            if (checks.evaluate(relation, baseTaggedObject)) {
                                success++;
                                if (loopInfo.type() == LoopInfoType.ANY) return true;
                            }
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (relationsNode.isArray()) {
                    relationsNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    if (!relationsNode.has("loop_info"))  expressions.add(parse(relationsNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    int candidates = 0;
                    int success    = 0;

                    for (Relation relation : taggedObject.getRelations()) {
                        candidates++;
                        boolean allOk = true;
                        for (Expression e : expressions) {
                            if (!e.evaluate(relation, baseTaggedObject)) {
                                allOk = false;
                                break;
                            }
                        }
                        if (allOk) {
                            success++;
                            if (loopInfo.type() == LoopInfoType.ANY) return true; // short-circuit
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            }
        }

        // ------ Parse relation members
        if (node.has("relation_members")) {
            JsonNode jsonNode = node.get("relation_members");
            LoopInfo loopInfo = this.parseLoopInfo(jsonNode);

            String role;
            if (node.has("role")) {
                role = jsonNode.path("role").asText();
            } else {
                role = null;
            }

            if (jsonNode.has("conditions") || jsonNode.has("checks")) {
                Expression conditions = parse(jsonNode.path("conditions"));
                Expression checks = parse(jsonNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> relationMembers = this.getRelationMembersAsFeature(taggedObject, role);
                    if (relationMembers.isEmpty()) return false;

                    int candidates = 0;
                    int success    = 0;

                    for (Feature relationMember : relationMembers) {
                        if (conditions.evaluate(relationMember, baseTaggedObject)) {
                            candidates++;
                            if (checks.evaluate(relationMember, baseTaggedObject)) {
                                success++;
                                if (loopInfo.type() == LoopInfoType.ANY) return true;
                            }
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (jsonNode.isArray()) {
                    jsonNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    if (!jsonNode.has("loop_info")) expressions.add(parse(jsonNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> relationMembers = this.getRelationMembersAsFeature(taggedObject, role);
                    if (relationMembers.isEmpty()) return false;

                    int candidates = 0;
                    int success    = 0;

                    for (Feature relationMember : relationMembers) {
                        candidates++;
                        boolean allOk = true;
                        for (Expression e : expressions) {
                            if (!e.evaluate(relationMember, baseTaggedObject)) {
                                allOk = false;
                                break;
                            }
                        }
                        if (allOk) {
                            success++;
                            if (loopInfo.type() == LoopInfoType.ANY) return true;
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            }
        }

        // ------ Parse way nodes
        if (node.has("way_nodes")) {
            JsonNode jsonNode = node.get("way_nodes");
            LoopInfo loopInfo = this.parseLoopInfo(jsonNode);

            if (jsonNode.has("conditions") || jsonNode.has("checks")) {
                Expression conditions = parse(jsonNode.path("conditions"));
                Expression checks = parse(jsonNode.path("checks"));

                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> wayNodes = this.geodataViewService.getWayNodesAsFeature(taggedObject);
                    if (wayNodes.isEmpty()) return false;

                    int candidates = 0;
                    int success    = 0;

                    for (Feature wayNode : wayNodes) {
                        if (conditions.evaluate(wayNode, baseTaggedObject)) {
                            candidates++;
                            if (checks.evaluate(wayNode, baseTaggedObject)) {
                                success++;
                                if (loopInfo.type() == LoopInfoType.ANY) return true;
                            }
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            } else {
                List<Expression> expressions = new ArrayList<>();
                if (jsonNode.isArray()) {
                    jsonNode.forEach(n -> expressions.add(parse(n)));
                } else {
                    if (!jsonNode.has("loop_info")) expressions.add(parse(jsonNode));
                }
                return (taggedObject, baseTaggedObject) -> {
                    List<Feature> wayNodes = this.geodataViewService.getWayNodesAsFeature(taggedObject);
                    if (wayNodes.isEmpty()) return false;

                    int candidates = 0;
                    int success    = 0;

                    for (Feature wayNode : wayNodes) {
                        candidates++;
                        boolean allOk = true;
                        for (Expression e : expressions) {
                            if (!e.evaluate(wayNode, baseTaggedObject)) {
                                allOk = false;
                                break;
                            }
                        }
                        if (allOk) {
                            success++;
                            if (loopInfo.type() == LoopInfoType.ANY) return true;
                        }
                    }

                    return evaluateLoop(success, candidates, loopInfo);
                };
            }
        }

        // Parse leafs, e.g. "tag_exists", "tag_regex_match", ...
        return this.registry.fromLeaf(node);
    }

    /**
     * Get relation members as feature list.
     */
    private List<Feature> getRelationMembersAsFeature(TaggedObject taggedObject, String role) {
        List<Feature> relationMemberFeatures = new ArrayList<>();

        if (taggedObject instanceof Relation relation) {
            if (relation.getMembers() != null && !relation.getMembers().isEmpty()) {
                DataSet relationMemberTaggedFeatures = Optional.ofNullable(
                                this.geodataViewService.getRelationMembers(relation.getOsmId(), null, null))
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

    private LoopInfo parseLoopInfo(JsonNode loopNode) {
        JsonNode loopInfoNode = loopNode.path("loop_info");

        if (loopInfoNode.isMissingNode() || loopInfoNode.isNull()) {
            return new LoopInfo(LoopInfoType.ALL, null, null);
        }

        try {
            ObjectMapper objectMapper = JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                    .build();

            LoopInfo loopInfo = objectMapper.treeToValue(loopInfoNode, LoopInfo.class);

            if (loopInfo.type().equals(LoopInfoType.COUNT)) {
                if (loopInfo.minCount() == null || loopInfo.maxCount() == null) {
                    throw new IllegalArgumentException("'minCount' and 'maxCount' can't be null.");
                }
            }

            return loopInfo;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("'loop_info' parse error.");
        }
    }

    /** Wendet die LoopInfo auf Zähler an. */
    private boolean evaluateLoop(int success, int candidates, LoopInfo loopInfo) {
        if (candidates == 0) return true;

        return switch (loopInfo.type()) {
            case ANY      -> success >= 1;
            case NONE     -> success == 0;
            case ALL      -> success == candidates;
            case COUNT    -> success >= loopInfo.minCount() && success <= loopInfo.maxCount();
        };
    }
}
