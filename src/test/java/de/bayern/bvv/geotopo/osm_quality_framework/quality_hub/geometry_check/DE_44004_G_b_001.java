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
 * 44004 'Gewässerachse' (ohne hatDirektUnten auf ein Bauwerk im Gewässerbereich) darf nur in einem Start- oder Endpunkt
 * die Umrissgeometrie eines Objekts 44001 'Fließgewässer', 44005 'Hafenbecken', 44006 'Stehendes Gewässer' oder 44007 'Meer' berühren.
 * Andere Objekte (Fließgewässer, etc.) mit hDU auf ein Bauwerk im Verkehrsbereich oder ein Bauwerk im Gewässerbereich sind bei der Prüfung zu ignorieren.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_44004_G_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.44004.G.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGewaesserachseAnStehendesGewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-126" lon="12.317768139260629" lat="49.872985841510825" version="0"/>
                        <node id="-127" lon="12.31779802792639" lat="49.87173377295284" version="0"/>
                        <node id="-128" lon="12.319800521786878" lat="49.87177229948244" version="0"/>
                        <node id="-129" lon="12.319770634261252" lat="49.873014734927" version="0"/>
                        <node id="-132" lon="12.316049580313996" lat="49.872321286698096" version="0"/>
                        <node id="-133" lon="12.317783557439627" lat="49.87233996135852" version="0"/>
                        <node id="-135" lon="12.317066237418532" lat="49.8723322359358" version="0"/>
                        <way id="-18" version="0">
                            <nd ref="-126"/>
                            <nd ref="-133"/>
                            <nd ref="-127"/>
                            <nd ref="-128"/>
                            <nd ref="-129"/>
                            <nd ref="-126"/>
                            <tag k="object_type" v="AX_StehendesGewaesser"/>
                        </way>
                        <way id="-19" version="0">
                            <nd ref="-132"/>
                            <nd ref="-135"/>
                            <nd ref="-133"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="fliessrichtung" v="TRUE"/>
                        </way>
                        <relation id="-7" version="0">
                            <member type="way" role="" ref="-19"/>
                            <tag k="object_type" v="AX_Wasserlauf"/>
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
    void createGewaesserachseInnerhalbStehendesGewaesser() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-132" lon="12.316049580313996" lat="49.872321286698096" version="0"/>
                        <node id="-133" lon="12.317783557439627" lat="49.87233996135852" version="0"/>
                        <node id="-135" lon="12.317066237418532" lat="49.8723322359358" version="0"/>
                        <node id="-142" lon="12.316976108430175" lat="49.8728895297538" version="0"/>
                        <node id="-143" lon="12.316961907650674" lat="49.87233111232066" version="0"/>
                        <node id="-144" lon="12.317020939718615" lat="49.87181082524647" version="0"/>
                        <node id="-145" lon="12.318933771002223" lat="49.871868612732705" version="0"/>
                        <node id="-146" lon="12.31882916238226" lat="49.872947317051505" version="0"/>
                        <way id="-19" version="0">
                            <nd ref="-132"/>
                            <nd ref="-143"/>
                            <nd ref="-135"/>
                            <nd ref="-133"/>
                            <tag k="object_type" v="AX_Gewaesserachse"/>
                            <tag k="fliessrichtung" v="TRUE"/>
                        </way>
                        <way id="-22" version="0">
                            <nd ref="-142"/>
                            <nd ref="-143"/>
                            <nd ref="-144"/>
                            <nd ref="-145"/>
                            <nd ref="-146"/>
                            <nd ref="-142"/>
                            <tag k="object_type" v="AX_StehendesGewaesser"/>
                        </way>
                        <relation id="-7" version="0">
                            <member type="way" role="" ref="-19"/>
                            <tag k="object_type" v="AX_Wasserlauf"/>
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
                .contains("Gewässerachse überlagert Fließgewässer, Hafenbecken, Stehendes Gewässer oder Meer.");
    }
}
