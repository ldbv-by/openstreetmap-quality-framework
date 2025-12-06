package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.All;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Any;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Criteria;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Leaf;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.Not;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.*;

public class CriteriaDeserializer extends ValueDeserializer<Criteria> {

    @Override
    public Criteria deserialize(JsonParser p, DeserializationContext ctx) {
        // Jackson 3: Tree über das Context lesen
        JsonNode n = ctx.readTree(p);

        if (n == null || n.isNull() || n.isMissingNode()) {
            return null;
        }

        return parseCriteria(n);
    }

    private Criteria parseCriteria(JsonNode n) {
        // ----- all -----
        if (n.has("all")) {
            List<Criteria> items = new ArrayList<>();
            for (JsonNode child : n.get("all")) {
                items.add(parseCriteria(child));
            }
            return new All(items);
        }

        // ----- any -----
        if (n.has("any")) {
            List<Criteria> items = new ArrayList<>();
            for (JsonNode child : n.get("any")) {
                items.add(parseCriteria(child));
            }
            return new Any(items);
        }

        // ----- not -----
        if (n.has("not")) {
            Criteria expr = parseCriteria(n.get("not"));
            return new Not(expr);
        }

        // ----- leaf -----
        JsonNode typeNode = n.get("type");
        String type = (typeNode != null && !typeNode.isNull())
                ? typeNode.asText()
                : null;

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("criteria leaf requires 'type'");
        }

        Map<String, Object> params;
        if (n.has("params") && n.get("params").isObject()) {
            params = toPlainMap(n.get("params"));
        } else {
            params = toPlainMap(n);
            params.remove("type");
        }

        return new Leaf(type, params);
    }

    private Map<String, Object> toPlainMap(JsonNode objNode) {
        Map<String, Object> result = new HashMap<>();

        for (var prop : objNode.properties()) {
            String key = prop.getKey();
            JsonNode value = prop.getValue();
            result.put(key, toPlainValue(value));
        }

        return result;
    }


    private Object toPlainValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isIntegralNumber()) {
            return node.asLong();
        }
        if (node.isFloatingPointNumber()) {
            return node.asDouble();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(child -> list.add(toPlainValue(child)));
            return list;
        }
        if (node.isObject()) {
            return toPlainMap(node);
        }
        return node.toString();
    }
}
