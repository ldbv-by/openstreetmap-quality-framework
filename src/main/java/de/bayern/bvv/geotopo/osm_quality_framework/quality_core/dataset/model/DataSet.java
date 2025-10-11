package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents all tagged objects.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataSet {
    private List<Feature> nodes = new ArrayList<>();
    private List<Feature> ways = new ArrayList<>();
    private List<Feature> areas = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();

    /**
     * Get all tagged objects.
     */
    public List<TaggedObject> getAll() {
        return Stream.of(nodes, ways, areas, relations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
