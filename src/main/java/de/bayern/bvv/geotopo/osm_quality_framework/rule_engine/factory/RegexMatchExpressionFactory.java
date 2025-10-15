package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Evaluates whether a tag value matches a regular expression.
 */
@Component
public class RegexMatchExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "regex_match";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String patternStr = json.path("pattern").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("regex_match: 'tag_key' is required");
        }

        Pattern pattern;
        if (patternStr == null || patternStr.isBlank()) {
            throw new IllegalArgumentException("regex_match: 'pattern' is required");
        } else {
            try {
                pattern = Pattern.compile(patternStr);
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("regex_match: invalid pattern: " + e.getMessage(), e);
            }
        }

        return taggedObject -> {
            String tagValue = taggedObject.getTags().get(tagKey);
            if (tagValue == null) return false;

            return pattern.matcher(tagValue).matches();
        };
    }
}
