package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Way primitive.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Way extends OsmPrimitive {

    /**
     * List of Way Node Ids.
     */
    private List<Nd> nodeRefs = new ArrayList<>();

    /**
     * A specific Way Node.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Nd {

        /**
         * Way Node identifier.
         */
        private Long ref;
    }
}
