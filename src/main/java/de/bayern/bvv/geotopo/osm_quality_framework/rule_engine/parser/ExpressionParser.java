package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.registry.ExpressionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public final class ExpressionParser {
    private final ExpressionRegistry registry;

    /**
     * Parse Condition.
     */
    public Expression parse(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode() || node.isEmpty()) return taggedObject -> true;

        // Parse operators
        if (node.has("all")) {
            List<Expression> expressions = new ArrayList<>();
            node.get("all").forEach(n -> expressions.add(parse(n)));
            return taggedObject -> { for  (Expression expression : expressions) if (!expression.evaluate(taggedObject)) return false; return true; };
        }

        if (node.has("any")) {
            List<Expression> expressions = new ArrayList<>();
            node.get("any").forEach(n -> expressions.add(parse(n)));
            return taggedObject -> { for  (Expression expression : expressions) if (expression.evaluate(taggedObject)) return true; return false; };
        }

        if (node.has("not")) {
            Expression expression = parse(node.get("not"));
            return taggedObject -> !expression.evaluate(taggedObject);
        }

        // Parse leafs, e.g. "tag_exists", "regex_match", ...
        return this.registry.fromLeaf(node);
    }

}
