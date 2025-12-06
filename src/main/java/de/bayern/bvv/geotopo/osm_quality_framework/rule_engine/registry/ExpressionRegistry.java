package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.registry;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory.ExpressionFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExpressionRegistry {
    private final Map<String, ExpressionFactory> factories;

    public ExpressionRegistry(List<ExpressionFactory> factories) {
        this.factories = factories.stream().collect(Collectors.toMap(ExpressionFactory::type, f -> f));
    }

    public Expression fromLeaf(JsonNode leaf) {
        var t = leaf.path("type").asText();
        var f = this.factories.get(t);
        if (f == null) throw new IllegalArgumentException("Unknown expression type: " + t);
        return f.create(leaf);
    }
}
