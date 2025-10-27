package de.bayern.bvv.geotopo.osm_quality_framework.rule_engine.api;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.TaggedObject;

/**
 * Represent an expression for conditions or checks.
 */
public interface Expression {
    boolean evaluate(TaggedObject taggedObject, TaggedObject baseTaggedObject);
}
