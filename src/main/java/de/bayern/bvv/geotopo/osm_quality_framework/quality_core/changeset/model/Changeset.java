package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * OSM changeset.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Changeset {

    /**
     * Changeset identifier.
     */
    private Long id;

    /**
     * Changeset version.
     */
    private String version;

    /**
     * Changeset generator e.g. JOSM.
     */
    private String generator;

    /**
     * OSM primitives created in this changeset.
     */
    private List<OsmPrimitive> createPrimitives = new ArrayList<>();

    /**
     * OSM primitives modified in this changeset.
     */
    private List<OsmPrimitive> modifyPrimitives = new ArrayList<>();

    /**
     * OSM primitives deleted in this changeset.
     */
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