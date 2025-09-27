package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.service;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangesetDataServiceIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    private ChangesetPrepareService changesetPrepareService;

    @Test
    void testPrepareChangesetMoveWayNode() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='63080' version='1' changeset='-1' lat='49.879514022' lon='12.340802590' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        var ids = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.areas WHERE changeset_id = ? ORDER BY osm_id",
                Long.class, changesetId);

        assertEquals(2, ids.size());
        assertEquals(List.of(7547L, 10732L), ids);
    }

}
