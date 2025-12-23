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
 * Kreuzt ein Objekt 44004 'Gewässerachse' ein Objekt der Objektart 42003 'Strassenachse', 42014 'Bahnstrecke', 42005 'Fahrbahnachse', 42008 'Fahrwegachse', 53003 'Weg, Pfad, Steig' oder 53006 'Gleis'
 * (alle jeweils ohne Relation hatDirektUnten auf ein 53001 'Bauwerk im Verkehrsbereich' oder 53009 'Bauwerk im Gewässerbereich' mit BWF 2030, 2040, 2050, 2060, 2080)
 * muss 44004 'Gewässerachse' eine Relation hatDirektUnten auf 53001 'Bauwerk im Verkehrsbereich' oder 53009 'Bauwerk im Gewässerbereich' BWF 2010, 2011, 2012, 2013, 2070, 2090 besitzen oder
 * am Kreuzungspunkt ein punktförmiges Objekt 53002 'Straßenverkehrsanlage' mit 'Art' 2000 'Furt' liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_44004_F_a_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.44004.F.a.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGewaesserachseMitHDUVonGleisOhneHDUGeschnitten() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.317196154274585" lat="49.87469185695703" version="0"/>
                        <node id="-14" lon="12.31731323996989" lat="49.873375531974496" version="0"/>
                        <node id="-19" lon="12.315732585564646" lat="49.87399596997379" version="0"/>
                        <node id="-20" lon="12.317256311598847" lat="49.87401554834501" version="0"/>
                        <node id="-21" lon="12.318741032357297" lat="49.8740378911641" version="0"/>
                        <way id="-4" version="0">
                            <nd ref="-13"/>
                            <nd ref="-20"/>
                            <nd ref="-14"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="breiteDesGewaessers" v="12"/>
                            <tag k="fliessrichtung" v="FALSE"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Gleis"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-13"/>
                            <nd ref="-20"/>
                            <nd ref="-14"/>
                            <tag k="object_type" v="AX_BauwerkImGewaesserbereich"/>
                            <tag k="bauwerksfunktion" v="2010"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-8"/>
                            <member type="way" role="over" ref="-4"/>
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
    void createGewaesserachseOhneHDUVonGleisMitHDUGeschnitten() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.317196154274585" lat="49.87469185695703" version="0"/>
                        <node id="-14" lon="12.31731323996989" lat="49.873375531974496" version="0"/>
                        <node id="-19" lon="12.315732585564646" lat="49.87399596997379" version="0"/>
                        <node id="-20" lon="12.317256311598847" lat="49.87401554834501" version="0"/>
                        <node id="-21" lon="12.318741032357297" lat="49.8740378911641" version="0"/>
                        <way id="-4" version="0">
                            <nd ref="-13"/>
                            <nd ref="-20"/>
                            <nd ref="-14"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="breiteDesGewaessers" v="12"/>
                            <tag k="fliessrichtung" v="FALSE"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Gleis"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-19"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                            <tag k="bauwerksfunktion" v="1800"/>
                        </way>
                        <relation id="-1" version="0">
                            <member type="way" role="under" ref="-8"/>
                            <member type="way" role="over" ref="-6"/>
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
    void createGewaesserachseOhneHDUVonGleisOhneHDUGeschnitten() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.317196154274585" lat="49.87469185695703" version="0"/>
                        <node id="-14" lon="12.31731323996989" lat="49.873375531974496" version="0"/>
                        <node id="-19" lon="12.315732585564646" lat="49.87399596997379" version="0"/>
                        <node id="-20" lon="12.317256311598847" lat="49.87401554834501" version="0"/>
                        <node id="-21" lon="12.318741032357297" lat="49.8740378911641" version="0"/>
                        <way id="-4" version="0">
                            <nd ref="-13"/>
                            <nd ref="-20"/>
                            <nd ref="-14"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="breiteDesGewaessers" v="12"/>
                            <tag k="fliessrichtung" v="FALSE"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Gleis"/>
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
                .contains("Gewässerachse kreuzt Verkehrsweg ohne BauwerkImVerkehrsbereich, BauwerkImGewaesserbereich oder Furt.");
    }

    @Test
    void createGewaesserachseOhneHDUVonGleisOhneHDUGeschnittenMitFurt() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-13" lon="12.317196154274585" lat="49.87469185695703" version="0"/>
                        <node id="-14" lon="12.31731323996989" lat="49.873375531974496" version="0"/>
                        <node id="-19" lon="12.315732585564646" lat="49.87399596997379" version="0"/>
                        <node id="-21" lon="12.318741032357297" lat="49.8740378911641" version="0"/>
                        <node id="-20" lon="12.317256311598847" lat="49.87401554834501" version="0">
                            <tag k="object_type" v="AX_Straßenverkehrsanlage"/>
                            <tag k="art" v="2000"/>
                        </node>
                        <way id="-4" version="0">
                            <nd ref="-13"/>
                            <nd ref="-20"/>
                            <nd ref="-14"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="breiteDesGewaessers" v="12"/>
                            <tag k="fliessrichtung" v="FALSE"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-19"/>
                            <nd ref="-20"/>
                            <nd ref="-21"/>
                            <tag k="object_type" v="AX_Gleis"/>
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
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }
}
