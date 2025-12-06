package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.util.JsonUtils;
import tools.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Compares a date from a tag against another tag's date or a fixed date.
 */
@Component
public class DateCompareExpressionFactory implements ExpressionFactory {

    /**
     * Defines the unique rule type.
     */
    @Override
    public String type() {
        return "date_compare";
    }

    /**
     * Defines the possible rule parameters.
     */
    private record RuleParams (
            String tagKey,
            String operator,
            String compareTagKey,
            String compareValue
    ) {}

    /**
     * Defines the rule parameters and the execution block of a rule.
     */
    @Override
    public Expression create(JsonNode json) {

        // ----- Parse rule params ------
        RuleParams params = this.parseParams(json);

        // ----- Execute rule ------
        return (taggedObject, baseTaggedObject) -> {
            String tagAsInstantString = taggedObject.getTags().get(params.tagKey);
            Instant tagAsInstant = toInstant(tagAsInstantString)
                    .orElseThrow(() -> new IllegalArgumentException(type() + ": value '" + tagAsInstantString + "' for 'tag_key' '" + params.tagKey + "' cannot be parsed"));

            String compareAsInstantString = !(params.compareValue().isEmpty()) ? params.compareValue() : taggedObject.getTags().get(params.compareTagKey);
            Instant compareAsInstant =  toInstant(compareAsInstantString)
                    .orElseThrow(() -> new IllegalArgumentException(type() + ": value '" + compareAsInstantString + "' for " +
                                                                    (!(params.compareValue().isEmpty()) ? "'compare_value'" : "'compare_tag_key' '" + params.compareTagKey + "' cannot be parsed")));

            int result = tagAsInstant.compareTo(compareAsInstant);

            return switch (params.operator) {
                case "<" -> result < 0;
                case "<=" -> result <= 0;
                case ">" -> result > 0;
                case ">=" -> result >= 0;
                case "==" -> result == 0;
                case "!=" -> result != 0;
                default -> throw new IllegalArgumentException(type() + ": unsupported operator: " + params.operator);
            };
        };
    }

    /**
     * Parse rule parameters.
     */
    private RuleParams parseParams(JsonNode json) {
        String tagKey = JsonUtils.asString(json, "tag_key", type());
        String operator = JsonUtils.asString(json,"operator", type());
        String compareTagKey = JsonUtils.asOptionalString(json, "compare_tag_key");
        String compareValue = JsonUtils.asOptionalString(json, "compare_value");

        boolean hasCompareTagKey = !(compareTagKey.isEmpty());
        boolean hasCompareValue = !(compareValue.isEmpty());

        if (!hasCompareTagKey && !hasCompareValue) {
            throw new IllegalArgumentException(type() + ": 'compare_tag_key' or 'compare_value' is required");
        }

        if (hasCompareTagKey && hasCompareValue) {
            throw new IllegalArgumentException(type() + ": 'compare_tag_key' and 'compare_value' together are not allowed");
        }

        return new RuleParams(
                tagKey, operator, compareTagKey, compareValue
        );
    }

    /**
     * Instant Helper.
     */
    private static Optional<Instant> toInstant(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        try { return Optional.of(Instant.parse(value)); } catch (Exception ignored) {}
        try { return Optional.of(java.time.OffsetDateTime.parse(value).toInstant()); } catch (Exception ignored) {}
        try { return Optional.of(java.time.LocalDateTime.parse(value).toInstant(java.time.ZoneOffset.UTC)); } catch (Exception ignored) {}
        try { return Optional.of(java.time.LocalDate.parse(value).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()); } catch (Exception ignored) {}
        try { return Optional.of(java.time.LocalDateTime.parse(value,
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                .toInstant(java.time.ZoneOffset.UTC)); } catch (Exception ignored) {}
        try { return Optional.of(java.time.LocalDateTime.parse(value,
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                .toInstant(java.time.ZoneOffset.UTC)); } catch (Exception ignored) {}
        try { return Optional.of(java.time.LocalDate.parse(value,
                        java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .atStartOfDay(java.time.ZoneOffset.UTC).toInstant()); } catch (Exception ignored) {}
        return Optional.empty();
    }
}
