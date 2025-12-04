package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.dto;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Rule;

public record RuleEvaluationDto(
        TaggedObject taggedObject,
        Rule rule
) {
}
