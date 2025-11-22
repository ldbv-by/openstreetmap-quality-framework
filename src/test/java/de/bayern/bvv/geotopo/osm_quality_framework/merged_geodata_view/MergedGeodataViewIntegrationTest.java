package de.bayern.bvv.geotopo.osm_quality_framework.merged_geodata_view;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import de.bayern.bvv.geotopo.osm_quality_framework.merged_geodata_view.api.MergedGeodataView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MergedGeodataViewIntegrationTest extends DatabaseIntegrationTest {
    @Autowired
    private MergedGeodataView mergedGeodataView;

    @Autowired
    private OsmGeometriesService osmGeometriesService;

    @Test
    void testGetDataSetBySpatialRelation_Within() {
        // Arrange
        DataSetFilter dataSetFilter = new DataSetFilter(
            null, null, null, new OsmIds(null, Set.of(779L), null, null), null, null);

        DataSetDto dataSetDto = this.osmGeometriesService.getDataSet(dataSetFilter);
        FeatureDto featureDto = dataSetDto.ways().getFirst();

        // Act
        DataSetDto resultDataSet = this.mergedGeodataView
                .getDataSetBySpatialRelation(featureDto, Set.of(SpatialOperator.WITHIN),
                        new DataSetFilter(null, null, null, null, null, null), false);

        // Assert
        assertThat(resultDataSet.nodes().size())
                .withFailMessage("Dataset composition: expected nodes=0 but was %d", resultDataSet.nodes().size())
                .isEqualTo(0);

        assertThat(resultDataSet.ways().size())
                .withFailMessage("Dataset composition: expected ways=1 but was %d", resultDataSet.ways().size())
                .isEqualTo(1);

        assertThat(resultDataSet.areas().size())
                .withFailMessage("Dataset composition: expected areas=0 but was %d", resultDataSet.areas().size())
                .isEqualTo(0);

        assertThat(resultDataSet.relations().size())
                .withFailMessage("Dataset composition: expected relations=0 but was %d", resultDataSet.relations().size())
                .isEqualTo(0);

        FeatureDto resultFeatureDto = resultDataSet.ways().getFirst();
        assertThat(resultFeatureDto.osmId()).isEqualTo(3640L);
        assertThat(resultFeatureDto.objectType()).isEqualTo("AX_Strassenachse");
    }
}