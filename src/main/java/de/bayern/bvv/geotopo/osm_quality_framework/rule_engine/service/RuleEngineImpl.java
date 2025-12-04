package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.service;

import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api.RuleEngine;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.dto.RuleEvaluationDto;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.Expression;
import de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.parser.ExpressionParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for validating rules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngineImpl implements RuleEngine {

    private final ExpressionParser expressionParser;

    @Override
    public boolean evaluate(RuleEvaluationDto eval) {
        log.info("{}: start rule={}", eval.rule().getType(), eval.rule().getId());
        long startTime = System.currentTimeMillis();

        Expression conditions = this.expressionParser.parse(eval.rule().getExpression().path("conditions"));
        Expression checks = this.expressionParser.parse(eval.rule().getExpression().path("checks"));

        if (conditions.evaluate(eval.taggedObject(), eval.taggedObject())) {
            if (!checks.evaluate(eval.taggedObject(), eval.taggedObject())) {
                log.info("{}: finish rule={}, time={} ms", eval.rule().getType(), eval.rule().getId(), System.currentTimeMillis() - startTime);
                return false;
            }
        }

        log.info("{}: finish rule={}, time={} ms", eval.rule().getType(), eval.rule().getId(), System.currentTimeMillis() - startTime);
        return true;
    }
}
