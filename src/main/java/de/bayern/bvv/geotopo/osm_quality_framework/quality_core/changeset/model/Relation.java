package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation primitive.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Relation extends OsmPrimitive {

    /**
     * List of relation members.
     */
    private List<Member> members = new ArrayList<>();

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
