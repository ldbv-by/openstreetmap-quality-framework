package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.mapper;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.RuleDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.ObjectType;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import lombok.experimental.UtilityClass;

/**
 * Mapping between {@link ObjectType} and {@link ObjectTypeDto}.
 */
@UtilityClass
public class RuleMapper {

    /**
     * Map rule dto to domain.
     */
    public Rule toDomain(RuleDto ruleDto) {
        if (ruleDto == null) return null;

        Rule rule = new Rule();
        rule.setId(ruleDto.id());
        rule.setType(ruleDto.type());
        rule.setExpression(ruleDto.expression());
        rule.setErrorText(ruleDto.errorText());

        return rule;
    }

    /**
     * Map rule domain to dto.
     */
    public RuleDto toDto(Rule rule) {
        if (rule == null) return null;

        return new RuleDto(
                rule.getId(),
                rule.getType(),
                rule.getExpression(),
                rule.getErrorText());
    }
}
