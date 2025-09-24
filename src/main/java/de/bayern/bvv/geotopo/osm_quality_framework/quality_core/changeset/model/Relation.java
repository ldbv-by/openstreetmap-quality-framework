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
public class Relation extends OsmPrimitive {
    private List<Member> members = new ArrayList<>();

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Member {
        private String type;
        private Long ref;
        private String role;
    }

}
