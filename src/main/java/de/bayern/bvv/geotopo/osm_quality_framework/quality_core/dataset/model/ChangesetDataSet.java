package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents all tagged objects.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangesetDataSet {
    private DataSet create = new DataSet();
    private DataSet modify = new DataSet();
    private DataSet delete = new DataSet();

    public Set<TaggedObject> getCreatedAndModified() {
        return Stream.concat(
                        Optional.ofNullable(this.create)
                                .map(DataSet::getAll).stream().flatMap(Collection::stream),
                        Optional.ofNullable(this.modify)
                                .map(DataSet::getAll).stream().flatMap(Collection::stream)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
