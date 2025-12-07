package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.mapper;


import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.entity.*;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;
import lombok.experimental.UtilityClass;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Mapping between {@link RuleEntity} and {@link Rule}.
 */
@UtilityClass
public class RuleEntityMapper {

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    /**
     * Map tag to domain.
     */
    public Rule toDomain(RuleEntity ruleEntity) {
        if (ruleEntity == null) return null;

        Rule rule = new Rule();
        rule.setId(ruleEntity.getId());
        rule.setType(ruleEntity.getType());
        rule.setExpression(objectMapper.valueToTree(ruleEntity.getExpression()));
        rule.setErrorText(ruleEntity.getErrorText());

        return rule;
    }
}
