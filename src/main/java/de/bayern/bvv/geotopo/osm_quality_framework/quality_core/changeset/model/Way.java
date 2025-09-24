package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Way extends OsmPrimitive {
    private List<Nd> nodeRefs = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Nd {
        private Long ref;
    }
}
