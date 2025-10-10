package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Object Type Relation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relation {
    private ObjectType objectType;
    private Multiplicity multiplicity;
    private List<Member> members = new ArrayList<>();

    /**
     * A specific relation member.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Member {

        /**
         * Member type, e.g. 'N', 'W', 'R', '*'.
         */
        private String type;

        /**
         * Member role, e.g. 'outer', 'inner', '*'.
         */
        private String role;

        /**
         * Member multiplicity.
         */
        private Multiplicity multiplicity;
    }
}
