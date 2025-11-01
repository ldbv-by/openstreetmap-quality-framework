package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CriteriaDeserializer extends StdDeserializer<Criteria> {

    public CriteriaDeserializer() {
        super(Criteria.class);
    }

    @Override
    public Criteria deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode n = codec.readTree(p);

        if (n == null || n.isNull() || n.isMissingNode()) return null;

        ObjectMapper om = (codec instanceof ObjectMapper)
                ? (ObjectMapper) codec
                : new ObjectMapper();

        TypeFactory tf = om.getTypeFactory();

        if (n.has("all")) {
            JavaType listType = tf.constructCollectionType(List.class, Criteria.class);
            List<Criteria> items = om.convertValue(n.get("all"), listType);
            return new All(items);
        }
        if (n.has("any")) {
            JavaType listType = tf.constructCollectionType(List.class, Criteria.class);
            List<Criteria> items = om.convertValue(n.get("any"), listType);
            return new Any(items);
        }
        if (n.has("not")) {
            Criteria expr = om.convertValue(n.get("not"), Criteria.class);
            return new Not(expr);
        }

        String type = n.path("type").asText(null);
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("criteria leaf requires 'type'");
        }

        Map<String, Object> params;
        if (n.has("params") && n.get("params").isObject()) {
            params = om.convertValue(n.get("params"), new TypeReference<>() {
            });
        } else {
            params = om.convertValue(n, new TypeReference<>() {
            });
            params.remove("type");
        }

        return new Leaf(type, params);
    }
}
