package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

public record LoopInfo(
        LoopInfoType type,
        Integer minCount,
        Integer maxCount
) {}
