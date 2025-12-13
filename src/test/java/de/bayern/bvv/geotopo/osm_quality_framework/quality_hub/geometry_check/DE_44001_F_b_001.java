package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * 44001 'Fließgewässer' darf keine gemeinsame Kante mit 42003 'Straßenachse', 42005 'Fahrbahnachse', 42008 'Fahrwegachse', 42014 'Bahnstrecke',
 * 53003 'WegPfadSteig' oder 53006 'Gleis' haben, es sei denn, die Objekte haben eine Relation hatDirektUnten zu einem Objekt
 * 53001 'Bauwerk im Verkehrsbereich' oder 53009 'Bauwerk im Gewässerbereich' oder liegen an einem linienförmigen Objekt
 * 53002 'Straßenverkehrsanlage' mit 'Art' 2000 'Furt'.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_44001_F_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.44001.F.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createFliessgewaesserMitStrassenachseMitRelationZuBauwerk() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-19" lon="12.324354388416007" lat="49.87831289713437" version="0"/>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <node id="-22" lon="12.32426565405197" lat="49.876965936510956" version="0"/>
                        <node id="-25" lon="12.325261002609867" lat="49.8783175156038" version="0"/>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-25"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <nd ref="-22"/>
                            <nd ref="-19"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-8"/>
                            <member type="way" role="over" ref="-7"/>
                            <tag k="object_type" v="AA_hatDirektUnten"/>
                        </relation>
                    </create>
                    <modify/>
                    <delete if-unused="true"/>
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
    void createFliessgewaesserMitStrassenachseOhneRelationZuBauwerk() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-19" lon="12.324354388416007" lat="49.87831289713437" version="0"/>
                        <node id="-20" lon="12.326848805302411" lat="49.878325604179174" version="0"/>
                        <node id="-21" lon="12.326878383674492" lat="49.8769341681193" version="0"/>
                        <node id="-22" lon="12.32426565405197" lat="49.876965936510956" version="0"/>
                        <node id="-25" lon="12.325261002609867" lat="49.8783175156038" version="0"/>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-25"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <nd ref="-22"/>
                            <nd ref="-19"/>
                            <tag k="object_type" v="AX_Fliessgewaesser"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                    </create>
                    <modify/>
                    <delete if-unused="true"/>
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

        QualityServiceResultDto geometryCheck = qualityHubResultDto.qualityServiceResults().stream()
                .filter(s -> "geometry-check".equals(s.qualityServiceId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("QualityService 'geometry-check' not found"));

        assertThat(geometryCheck.isValid()).withFailMessage("Expected the result is not valid, but it was valid.").isFalse();

        assertThat(geometryCheck.errors())
                .as("Errors of 'geometry-check' must not be empty")
                .isNotEmpty();

        assertThat(geometryCheck.errors())
                .extracting(QualityServiceErrorDto::errorText)
                .as("Error text of 'geometry-check'")
                .contains("Ein Fließgewässer darf keine gemeinsamen Kanten mit Verkehrsachsen führen, wenn nicht eine Relation zu einem Bauwerk oder einer Straßenverkehrsanlage besteht.");
    }
}