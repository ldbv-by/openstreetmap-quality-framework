package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.dto.RuleEvaluationDto;

public interface RuleEngine {
    boolean evaluate(RuleEvaluationDto ruleEvaluationDto);
}
