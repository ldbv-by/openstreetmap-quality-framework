package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.factory;

import com.fasterxml.jackson.databind.JsonNode;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.ExpressionFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

/**
 * Compares a date from a tag against another tag's date or a fixed date.
 */
@Component
public class DateCompareExpressionFactory implements ExpressionFactory {

    @Override
    public String type() {
        return "date_compare";
    }

    @Override
    public Expression create(JsonNode json) {
        String tagKey = json.path("tag_key").asText();
        String operator = json.path("operator").asText();
        String compareTagKey = json.path("compare_tag_key").asText();
        String compareValue = json.path("compare_value").asText();

        if (tagKey == null || tagKey.isBlank()) {
            throw new IllegalArgumentException("date_compare: 'tag_key' is required");
        }

        if (operator == null || operator.isBlank()) {
            throw new IllegalArgumentException("date_compare: 'operator' is required");
        }

        boolean hasCompareTagKey = !(compareTagKey == null || compareTagKey.isBlank());
        boolean hasCompareValue = !(compareValue == null || compareValue.isBlank());

        if (!hasCompareTagKey && !hasCompareValue) {
            throw new IllegalArgumentException("date_compare: 'compare_tag_key' or 'compare_value' is required");
        }

        if (hasCompareTagKey && hasCompareValue) {
            throw new IllegalArgumentException("date_compare: 'compare_tag_key' and 'compare_value' together are not allowed");
        }

        return (taggedObject, baseTaggedObject) -> {
            String tagInstantStr = taggedObject.getTags().get(tagKey);
            Instant tagInstant =  toInstant(tagInstantStr)
                    .orElseThrow(() -> new IllegalArgumentException("date_compare: value '" + tagInstantStr + "' for 'tag_key' '" + tagKey + "' cannot be parsed"));

            String compareInstantStr = hasCompareValue ? compareValue : taggedObject.getTags().get(compareTagKey);
            Instant compareInstant =  toInstant(compareInstantStr)
                    .orElseThrow(() -> new IllegalArgumentException("date_compare: value '" + compareInstantStr + "' for " +
                                                                    (hasCompareValue ? "'compare_value'" : "'compare_tag_key' '" + compareTagKey + "' cannot be parsed")));

            int result = tagInstant.compareTo(compareInstant);

            return switch (operator) {
                case "<" -> result < 0;
                case "<=" -> result <= 0;
                case ">" -> result > 0;
                case ">=" -> result >= 0;
                case "==" -> result == 0;
                case "!=" -> result != 0;
                default -> throw new IllegalArgumentException("date_compare: unsupported operator: " + operator);
            };
        };
    }

    /**
     * Date helper.
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
