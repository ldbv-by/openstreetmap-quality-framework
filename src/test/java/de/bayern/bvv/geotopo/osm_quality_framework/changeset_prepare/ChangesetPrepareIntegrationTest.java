package de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare;

import de.bayern.bvv.geotopo.osm_quality_framework.changeset_prepare.spi.ChangesetPrepareService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.dto.ChangesetDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.changeset.util.ChangesetXml;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ChangesetPrepareIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    private ChangesetPrepareService changesetPrepareService;

    @Test
    void testPrepareChangesetMoveWayNode() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='63079' version='1' changeset='-1' lat='49.87954625197' lon='12.34069470293' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId, 7547L, 10732L),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 7547L, 10732L),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetMoveInnerWayNode() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='61600' version='1' changeset='-1' lat='49.88090737714' lon='12.33658789336' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId, 873L, 7421L),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 873L, 7421L),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetMoveInnerWayNodeByMultiInners() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='62341' version='1' changeset='-1' lat='49.88828136656' lon='12.34478623824' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId, 7476L, 884L),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 7476L, 884L),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetMoveOuterWayNode() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='33244' version='1' changeset='-1' lat='49.88137576909' lon='12.33601618346' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId, 3943L),
                () -> assertAreas(changesetId, 873L, 10737L),
                () -> assertRelations(changesetId, 4L, 1467L),
                () -> assertRelationMembers(changesetId, 3943L, 5813L, 5749L, 5629L, 3950L, 3949L,
                        3948L, 3947L, 3946L, 3945L, 3944L, 3942L, 3941L, 3640L, 4L, 10L, 33L, 34L, 102L, 103L, 406L),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 3943L, 873L, 10737L, 4L, 1467L),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetMoveStreetNode() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <node id='42066' version='1' changeset='-1' lat='49.87922001465' lon='12.33913366442' />
                </modify>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId, 5389L),
                () -> assertAreas(changesetId, 10800L, 11290L),
                () -> assertRelations(changesetId, 404L),
                () -> assertRelationMembers(changesetId, 5388L, 5389L, 5390L, 5391L, 5712L, 5429L),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 5389L,10800L, 11290L, 404L),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetCreateObservationTower() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25468' changeset='-1' lat='49.88025286581' lon='12.34805233434'>
                    <tag k='BWF_bauwerksfunktion' v='1003' />
                    <tag k='object_type' v='AX_Turm' />
                  </node>
                </create>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId, -25468L),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE", -25468L),
                () -> assertChangesetObjects(changesetId, "MODIFY"),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetCreateMultipolygon() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                            <create>
                              <node id='-25531' changeset='-1' lat='49.87984858601' lon='12.34863490422' />
                              <node id='-25530' changeset='-1' lat='49.87992078037' lon='12.34957599672' />
                              <node id='-25529' changeset='-1' lat='49.88035394425' lon='12.34959840368' />
                              <node id='-25528' changeset='-1' lat='49.88038282171' lon='12.34879175297' />
                              <way id='-766' changeset='-1'>
                                <nd ref='-25528' />
                                <nd ref='-25529' />
                                <nd ref='-25530' />
                                <nd ref='-25531' />
                                <nd ref='-25528' />
                                <tag k='WDM_widmung' v='1340' />
                                <tag k='object_type' v='AX_StehendesGewaesser' />
                              </way>
                              <relation id='-62' changeset='-1'>
                                <member type='way' ref='9342' role='outer' />
                                <member type='way' ref='-766' role='inner' />
                                <tag k='OID_identifikator' v='DEBYBDLMJW00030W' />
                                <tag k='VEG_vegetationsmerkmal' v='1010' />
                                <tag k='object_type' v='AX_Landwirtschaft' />
                                <tag k='type' v='multipolygon' />
                              </relation>
                            </create>
                            <modify>
                              <way id='9342' version='1' changeset='-1'>
                                <nd ref='54715' />
                                <nd ref='54714' />
                                <nd ref='54713' />
                                <nd ref='54712' />
                                <nd ref='54711' />
                                <nd ref='54710' />
                                <nd ref='54709' />
                                <nd ref='54708' />
                                <nd ref='54707' />
                                <nd ref='54706' />
                                <nd ref='54705' />
                                <nd ref='34474' />
                                <nd ref='34473' />
                                <nd ref='34472' />
                                <nd ref='34471' />
                                <nd ref='34470' />
                                <nd ref='34469' />
                                <nd ref='34468' />
                                <nd ref='34467' />
                                <nd ref='34466' />
                                <nd ref='34465' />
                                <nd ref='34464' />
                                <nd ref='34463' />
                                <nd ref='34462' />
                                <nd ref='34461' />
                                <nd ref='34460' />
                                <nd ref='34459' />
                                <nd ref='34458' />
                                <nd ref='34457' />
                                <nd ref='34456' />
                                <nd ref='34455' />
                                <nd ref='34454' />
                                <nd ref='34453' />
                                <nd ref='34452' />
                                <nd ref='34451' />
                                <nd ref='34450' />
                                <nd ref='34449' />
                                <nd ref='34448' />
                                <nd ref='34447' />
                                <nd ref='34446' />
                                <nd ref='34445' />
                                <nd ref='54463' />
                                <nd ref='54462' />
                                <nd ref='45011' />
                                <nd ref='45012' />
                                <nd ref='45013' />
                                <nd ref='45014' />
                                <nd ref='45015' />
                                <nd ref='45016' />
                                <nd ref='45017' />
                                <nd ref='45018' />
                                <nd ref='45019' />
                                <nd ref='45020' />
                                <nd ref='45021' />
                                <nd ref='45022' />
                                <nd ref='45023' />
                                <nd ref='45024' />
                                <nd ref='45025' />
                                <nd ref='45026' />
                                <nd ref='45027' />
                                <nd ref='45028' />
                                <nd ref='45029' />
                                <nd ref='45030' />
                                <nd ref='45031' />
                                <nd ref='45032' />
                                <nd ref='45033' />
                                <nd ref='45034' />
                                <nd ref='45035' />
                                <nd ref='45036' />
                                <nd ref='45037' />
                                <nd ref='45038' />
                                <nd ref='45039' />
                                <nd ref='45040' />
                                <nd ref='45041' />
                                <nd ref='45042' />
                                <nd ref='45043' />
                                <nd ref='45044' />
                                <nd ref='45045' />
                                <nd ref='45046' />
                                <nd ref='45047' />
                                <nd ref='45048' />
                                <nd ref='45049' />
                                <nd ref='45050' />
                                <nd ref='74703' />
                                <nd ref='74704' />
                                <nd ref='74705' />
                                <nd ref='74706' />
                                <nd ref='74707' />
                                <nd ref='74708' />
                                <nd ref='74709' />
                                <nd ref='74710' />
                                <nd ref='74711' />
                                <nd ref='74712' />
                                <nd ref='74713' />
                                <nd ref='74714' />
                                <nd ref='74715' />
                                <nd ref='74716' />
                                <nd ref='31244' />
                                <nd ref='31245' />
                                <nd ref='31246' />
                                <nd ref='54715' />
                              </way>
                            </modify>
                            </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId, -62L, -766L),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE", -62L, -766L),
                () -> assertChangesetObjects(changesetId, "MODIFY"),
                () -> assertChangesetObjects(changesetId, "DELETE")
        );
    }

    @Test
    void testPrepareChangesetDeleteArea() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <delete>
                  <way id='11746' version='1' changeset='-1'/>
                </delete>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY"),
                () -> assertChangesetObjects(changesetId, "DELETE", 11746L)
        );
    }

    @Test
    void testPrepareChangesetDeleteInnerInMultipolygon() {
        // Arrange
        String osc = """
                <osmChange version="0.6" generator="JOSM">
                <modify>
                  <relation id='884' version='1' changeset='-1'>
                    <member type='way' ref='7472' role='inner' />
                    <member type='way' ref='7471' role='inner' />
                    <member type='way' ref='7473' role='inner' />
                    <member type='way' ref='7474' role='inner' />
                    <member type='way' ref='10495' role='outer' />
                    <tag k='OID_identifikator' v='DEBYBDLMJW0002rY' />
                    <tag k='VEG_vegetationsmerkmal' v='1020' />
                    <tag k='object_type' v='AX_Landwirtschaft' />
                    <tag k='type' v='multipolygon' />
                  </relation>
                </modify>
                <delete>
                  <way id='7476' version='1' changeset='-1'/>
                  <node id='62339' version='1' changeset='-1'/>
                  <node id='62340' version='1' changeset='-1'/>
                  <node id='62341' version='1' changeset='-1'/>
                  <node id='62342' version='1' changeset='-1'/>
                </delete>
                </osmChange>
                """;

        Long changesetId = 1L;
        ChangesetDto changesetDto = ChangesetXml.fromXml(osc);

        // Act
        this.changesetPrepareService.prepareChangeset(changesetId, changesetDto);

        // Assert
        assertAll(
                () -> assertNodes(changesetId),
                () -> assertWays(changesetId),
                () -> assertAreas(changesetId, 884L),
                () -> assertRelations(changesetId),
                () -> assertRelationMembers(changesetId),
                () -> assertChangesetObjects(changesetId, "CREATE"),
                () -> assertChangesetObjects(changesetId, "MODIFY", 884L),
                () -> assertChangesetObjects(changesetId, "DELETE", 7476L)
        );
    }

    private void assertNodes(Long changesetId, Long... expectedIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.nodes WHERE changeset_id = ?",
                Long.class, changesetId);
        assertListUnordered("Nodes in changeset", actual, List.of(expectedIds));
    }

    private void assertWays(Long changesetId, Long... expectedIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.ways WHERE changeset_id = ?",
                Long.class, changesetId);
        assertListUnordered("Ways in changeset", actual, List.of(expectedIds));
    }

    private void assertAreas(Long changesetId, Long... expectedIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.areas WHERE changeset_id = ?",
                Long.class, changesetId);
        assertListUnordered("Areas in changeset", actual, List.of(expectedIds));
    }

    private void assertRelations(Long changesetId, Long... expectedIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.relations WHERE changeset_id = ?",
                Long.class, changesetId);
        assertListUnordered("Relations in changeset", actual, List.of(expectedIds));
    }

    private void assertRelationMembers(Long changesetId, Long... expectedMemberIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT member_osm_id FROM changeset_data.relation_members WHERE changeset_id = ?",
                Long.class, changesetId);
        assertListUnordered("Relation Members in changeset", actual, List.of(expectedMemberIds));
    }

    private void assertChangesetObjects(Long changesetId, String operationType, Long... expectedIds) {
        List<Long> actual = this.jdbcTemplate.queryForList(
                "SELECT osm_id FROM changeset_data.changeset_objects WHERE changeset_id = ? AND operation_type = ?",
                Long.class, changesetId, operationType);
        assertListUnordered(operationType + " Changeset Objects in changeset", actual, List.of(expectedIds));
    }

    private static <T> void assertListUnordered(String description, List<T> actual, List<T> expected) {
        if (expected == null || expected.isEmpty()) {
            assertThat(actual).as(description).isEmpty();
        } else {
            assertThat(actual).as(description)
                    .hasSize(expected.size())
                    .containsExactlyInAnyOrderElementsOf(expected);
        }
    }

}
