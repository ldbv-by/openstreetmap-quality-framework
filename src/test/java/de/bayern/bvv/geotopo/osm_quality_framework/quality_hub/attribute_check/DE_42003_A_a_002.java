package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdV-Beschreibung:
 * Wenn bei einem Objekt 42003 der Attributart 'BVB' die Werteart 1000 'Überörtlicher Durchgangsverkehr' belegt ist,
 * schließt geometrisch beidseitig immer ein weiteres Objekt 42003 mit BVB 1000 an.
 * <p>
 * Wenn dabei ein REO 42003 AX_Strassenachse mit Attributbelegung BVB 1000 an ein REO 42005 AX_Fahrbahnachse geometrisch anschließt
 * das zu einem ZUSO AX_Strasse mit einem REO AX_Strassenachse gehört das die Attributbelegung BVB 1000 führt,
 * wird keine Fehlermeldung erzeugt.
 * <p>
 * Ein Objekt 42003 mit BVB 1000 das an einem Objekt 57002 AX_SchifffahrtslinieFaehrverkehr ART 1710 Autofährverkehr oder
 * 75009 Gebietsgrenze mit AGZ 7102 Landesgrenze endet, schließt geometrisch nur einseitig an einem weiteren Objekt 42003 mit BVB 1000 an.
 * <p>
 * Ist das zu untersuchende Objekt 42003 mit BVB 1000 Bestandteil eines ZUSO 42002 AX_Strasse mit FTR 2000,
 * bei dem an den zum ZUSO gehörenden Objekten 42005 AX_Fahrbahnachse jeweils
 * 1. an mindestens einem Ende ein Objekt 42003 AX_Strassenachse mit BVB 1000 anschließt,
 *    dessen Objektidentifikator unterschiedlich zu dem zu untersuchenden Objekt ist und
 *    das Bestandteil eines ZUSO 42002 AX_Strasse ohne FTR 2000 ist oder
 * 2. an beiden Enden weitere Objekte 42005 AX_Fahrbahnachse anschließen,
 * ist ebenfalls keine Fehlermeldung auszugeben.
 * {
 *     "conditions": { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" },
 *     "checks": {
 *         "all": [
 *             {
 *                 "way_nodes": {
 *                     "conditions": { "type": "way_node_compare", "index": "1" },
 *                     "checks": {
 *                         "type": "spatial_compare",
 *                         "operator": "touches",
 *                         "data_set_filter": {
 *                             "criteria": {
 *                                 "any": [
 *                                     {
 *                                         "all": [
 *                                             { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
 *                                             { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
 *                                         ]
 *                                     },
 *
 *                                     {
 *                                         "all": [
 *                                             { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SchifffahrtslinieFaehrverkehr" },
 *                                             { "type": "tag_equals", "tag_key": "art", "value": "1710" }
 *                                         ]
 *                                     },
 *
 *                                     {
 *                                         "all": [
 *                                             { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gebietsgrenze" },
 *                                             { "type": "tag_equals", "tag_key": "artDerGebietsgrenze", "value": "7102" }
 *                                         ]
 *                                     },
 *
 *                                     {
 *                                         "all": [
 *                                             { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fahrbahnachse" },
 *                                             {
 *                                                 "type": "relation_exists",
 *                                                 "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strasse" },
 *                                                 "relation_members": {
 *                                                     "criteria": {
 *                                                         "all": [
 *                                                             { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Strassenachse" },
 *                                                             { "type": "tag_equals", "tag_key": "besondereVerkehrsbedeutung", "value": "1000" }
 *                                                         ]
 *                                                     }
 *                                                 }
 *                                             }
 *                                         ]
 *                                     }
 *                                 ]
 *                             }
 *                         }
 *                     }
 *                 }
 *             }
 *
 *         ]
 *     }
 * }
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42003_A_a_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42003.A.a.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createStrassenachseMitFahrbahnachse() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <member type='way' ref='-2' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createStrassenachseMitFahrbahnachseUndOberflaechenmaterial() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                    <tag k='breiteDerFahrbahn' v='9' />
                    <tag k='oberflaechenmaterial' v='1220' />
                  </way>
                  <way id='-2' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Fahrbahnachse' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <member type='way' ref='-2' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        QualityServiceResultDto attributeCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "attribute-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'attribute-check' not found"));

        assertThat(attributeCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(attributeCheck.errors())
                .as("Errors of 'attribute-check' must not be empty")
                .isNotEmpty();

        assertThat(attributeCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'attribut-check'")
                .contains("Die Tags 'besondereFahrstreifen', 'breiteDerFahrbahn', 'funktion', 'anzahlDerFahrstreifen', 'zustand' und 'oberflaechenmaterial' dürfen nicht belegt sein, wenn eine 'AX_Strassenachse' in einer Relation 'AX_Strasse' mit 'AX_Fahrbahnachse' liegt.");
    }

    @Test
    void createStrassenachseOhneFahrbahnachseUndMitOberflaechenmaterial() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-1' changeset='-1' lat='49.87494356474' lon='12.32280142285' />
                  <node id='-2' changeset='-1' lat='49.87777772641' lon='12.32285980169' />
                  <way id='-1' changeset='-1'>
                    <nd ref='-1' />
                    <nd ref='-2' />
                    <tag k='object_type' v='AX_Strassenachse' />
                    <tag k='breiteDesVerkehrsweges' v='6' />
                    <tag k='breiteDerFahrbahn' v='9' />
                    <tag k='oberflaechenmaterial' v='1220' />
                  </way>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-1' role='' />
                    <tag k='widmung' v='1301' />
                    <tag k='object_type' v='AX_Strasse' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML)
                                .param("steps", String.join(",", stepsToValidate))
                                .param("rules", String.join(",", rulesToValidate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }
}