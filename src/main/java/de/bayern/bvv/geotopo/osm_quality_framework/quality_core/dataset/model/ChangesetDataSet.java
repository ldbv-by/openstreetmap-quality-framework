package de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents all tagged objects.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChangesetDataSet {
    private DataSet create;
    private DataSet modify;
    private DataSet delete;
}
