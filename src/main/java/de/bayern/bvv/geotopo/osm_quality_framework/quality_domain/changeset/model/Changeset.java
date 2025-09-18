package de.bayern.bvv.geotopo.osm_quality_framework.quality_domain.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Changeset {
    private Long id;
    private String version;
    private String generator;
    private List<OsmPrimitive> createPrimitives = new ArrayList<>();
    private List<OsmPrimitive> modifyPrimitives = new ArrayList<>();
    private List<OsmPrimitive> deletePrimitives = new ArrayList<>();

    /**
     * Get all primitives.
     */
    public List<OsmPrimitive> getAllPrimitives() {
        return Stream.of(this.createPrimitives, this.modifyPrimitives, this.deletePrimitives)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }
}