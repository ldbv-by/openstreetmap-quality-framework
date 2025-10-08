package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link RuleEntity} and {@link Rule}.
 */
@UtilityClass
public class RuleEntityMapper {

    /**
     * Map tag to domain.
     */
    public Rule toDomain(RuleEntity ruleEntity) {
        if (ruleEntity == null) return null;

        Rule rule = new Rule();
        rule.setId(ruleEntity.getId());
        rule.setType(ruleEntity.getType());
        rule.setExpression(ruleEntity.getExpression());
        rule.setErrorText(ruleEntity.getErrorText());

        return rule;
    }
}
