package de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.*;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import de.bayern.bvv.geotopo.osm_quality_framework.unified_data_provider.api.UnifiedDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UnifiedDataProviderIntegrationTest extends DatabaseIntegrationTest {
    @Autowired
    private UnifiedDataProvider unifiedDataProvider;

    @Autowired
    private OsmGeometriesService osmGeometriesService;

    @Test
    void testGetDataSetBySpatialRelation_Within() {
        // Arrange
        FeatureFilter featureFilter = new FeatureFilter(
                new OsmIds(null, Set.of(779L), null, null), null, null);

        DataSetDto dataSetDto = this.osmGeometriesService.getDataSet(featureFilter, null);
        FeatureDto featureDto = dataSetDto.ways().getFirst();

        // Act
        DataSetDto resultDataSet = this.unifiedDataProvider
                .getDataSetBySpatialRelation(featureDto, Set.of(SpatialOperator.WITHIN), null);

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