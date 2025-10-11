package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an OSM object without a direct geometric reference.
 */
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Relation extends TaggedObject {
    private List<Member> members = new LinkedList<>();

    /**
     * A specific relation member.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Member {

        /**
         * Member type, e.g. 'N', 'W', 'R'.
         */
        private String type;

        /**
         * Member id.
         */
        private Long ref;

        /**
         * Member role, e.g. 'outer', 'inner'.
         */
        private String role;
    }
}
