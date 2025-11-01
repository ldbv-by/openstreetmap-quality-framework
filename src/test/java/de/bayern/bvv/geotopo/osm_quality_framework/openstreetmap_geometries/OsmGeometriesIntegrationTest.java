package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_geometries.api.OsmGeometriesService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.DataSetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.dto.FeatureDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.DataSetFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.FeatureFilter;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.dataset.model.OsmIds;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OsmGeometriesIntegrationTest extends DatabaseIntegrationTest {
    @Autowired
    private OsmGeometriesService osmGeometriesService;

    @Test
    void testGetAreaByOsmId() {
        // Arrange
        DataSetFilter dataSetFilter = new DataSetFilter(
                null, null, null,
                new OsmIds(null, Set.of(10727L), null, null), null,null);

        // Act
        DataSetDto dataSetDto = this.osmGeometriesService.getDataSet(dataSetFilter);

        // Assert
        assertThat(dataSetDto.nodes().size())
                .withFailMessage("Dataset composition: expected nodes=0 but was %d", dataSetDto.nodes().size())
                .isEqualTo(0);

        assertThat(dataSetDto.ways().size())
                .withFailMessage("Dataset composition: expected ways=0 but was %d", dataSetDto.ways().size())
                .isEqualTo(0);

        assertThat(dataSetDto.areas().size())
                .withFailMessage("Dataset composition: expected areas=1 but was %d", dataSetDto.areas().size())
                .isEqualTo(1);

        assertThat(dataSetDto.relations().size())
                .withFailMessage("Dataset composition: expected relations=0 but was %d", dataSetDto.relations().size())
                .isEqualTo(0);

        FeatureDto featureDto = dataSetDto.areas().getFirst();
        assertThat(featureDto.osmId()).isEqualTo(10727L);
        assertThat(featureDto.objectType()).isEqualTo("AX_SportFreizeitUndErholungsflaeche");
        assertThat(featureDto.tags().size()).isEqualTo(3);
        assertThat(featureDto.relations().size()).isEqualTo(0);
    }

    @Test
    void testGetStreetByOsmId() {
        // Arrange
        DataSetFilter dataSetFilter = new DataSetFilter(
                null, null, null,
                new OsmIds(null, Set.of(3660L), null, null), null, null);

        // Act
        DataSetDto dataSetDto = this.osmGeometriesService.getDataSet(dataSetFilter);

        // Assert
        assertThat(dataSetDto.nodes().size())
                .withFailMessage("Dataset composition: expected nodes=0 but was %d", dataSetDto.nodes().size())
                .isEqualTo(0);

        assertThat(dataSetDto.ways().size())
                .withFailMessage("Dataset composition: expected ways=1 but was %d", dataSetDto.ways().size())
                .isEqualTo(1);

        assertThat(dataSetDto.areas().size())
                .withFailMessage("Dataset composition: expected areas=0 but was %d", dataSetDto.areas().size())
                .isEqualTo(0);

        assertThat(dataSetDto.relations().size())
                .withFailMessage("Dataset composition: expected relations=0 but was %d", dataSetDto.relations().size())
                .isEqualTo(0);

        FeatureDto featureDto = dataSetDto.ways().getFirst();
        assertThat(featureDto.osmId()).isEqualTo(3660L);
        assertThat(featureDto.objectType()).isEqualTo("AX_Strassenachse");
        assertThat(featureDto.tags().get("OID_identifikator")).isEqualTo("DEBYBDLMJW0003yU");
        assertThat(featureDto.relations().size()).isEqualTo(1);
        assertThat(featureDto.relations().getFirst().osmId()).isEqualTo(6);
        assertThat(featureDto.relations().getFirst().objectType()).isEqualTo("AX_Strasse");
        assertThat(featureDto.relations().getFirst().tags().get("OID_identifikator")).isEqualTo("DEBYBDLMJW00045Y");
    }

    @Test
    void testGetRelationMembersByOsmId() {
        // Act
        DataSetDto dataSetDto = this.osmGeometriesService.getRelationMembers(1087L, "under", null);

        // Assert
        assertThat(dataSetDto.nodes().size())
                .withFailMessage("Dataset composition: expected nodes=0 but was %d", dataSetDto.nodes().size())
                .isEqualTo(0);

        assertThat(dataSetDto.ways().size())
                .withFailMessage("Dataset composition: expected ways=1 but was %d", dataSetDto.ways().size())
                .isEqualTo(1);

        assertThat(dataSetDto.areas().size())
                .withFailMessage("Dataset composition: expected areas=0 but was %d", dataSetDto.areas().size())
                .isEqualTo(0);

        assertThat(dataSetDto.relations().size())
                .withFailMessage("Dataset composition: expected relations=0 but was %d", dataSetDto.relations().size())
                .isEqualTo(0);

        FeatureDto featureDto = dataSetDto.ways().getFirst();
        assertThat(featureDto.osmId()).isEqualTo(122L);
        assertThat(featureDto.objectType()).isEqualTo("AX_BauwerkImGewaesserbereich");
        assertThat(featureDto.tags().size()).isEqualTo(3);
        assertThat(featureDto.relations().size()).isEqualTo(1);
    }
}