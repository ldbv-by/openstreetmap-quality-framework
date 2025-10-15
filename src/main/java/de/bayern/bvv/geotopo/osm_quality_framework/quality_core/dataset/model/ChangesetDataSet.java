package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class ChangesetDataSet {
    private DataSet create = new DataSet();
    private DataSet modify = new DataSet();
    private DataSet delete = new DataSet();
}
