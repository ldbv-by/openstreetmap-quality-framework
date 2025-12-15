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
 * Kommunales Teilgebiet' darf nur geführt werden, wenn 'gemeindeteil' nicht in der Objektart AX_KommunalesGebiet verwendet wird.
 * <p>
 * Die Flächen der "Kommunalen Teilgebiete" überlagern die Flächen des Kommunalen Gebietes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_75012_G_a_003_G_b_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.75012.G.a.003_G.b.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createKommunalesTeilgebietMitGemeindeOhneGemeindeteil() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-43" lon="12.318275553002538" lat="49.87290616512915" version="0"/>
                        <node id="-44" lon="12.318275553002538" lat="49.871985174569666" version="0"/>
                        <node id="-47" lon="12.320012791937724" lat="49.871964108148156" version="0"/>
                        <node id="-48" lon="12.319984771954896" lat="49.87290917706879" version="0"/>
                        <node id="-52" lon="12.31910704533641" lat="49.87197509157784" version="0"/>
                        <node id="-68" lon="12.319141057154875" lat="49.8729076902973" version="0"/>
                        <way id="-7" version="0">
                            <nd ref="-43"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-44"/>
                            <nd ref="-52"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-9" version="0">
                            <nd ref="-48"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-43"/>
                            <nd ref="-68"/>
                            <nd ref="-48"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-11" version="0">
                            <nd ref="-52"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-12" version="0">
                            <nd ref="-43"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-13" version="0">
                            <nd ref="-68"/>
                            <nd ref="-43"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-14" version="0">
                            <nd ref="-52"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-15" version="0">
                            <nd ref="-47"/>
                            <nd ref="-52"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-16" version="0">
                            <nd ref="-47"/>
                            <nd ref="-48"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-17" version="0">
                            <nd ref="-48"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <relation id="-3" version="0">
                            <member type="way" role="" ref="-10"/>
                            <member type="way" role="" ref="-7"/>
                            <member type="way" role="" ref="-8"/>
                            <member type="way" role="" ref="-9"/>
                            <tag k="object_type" v="AX_KommunalesGebiet"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="gemeindekennzeichen:gemeinde" v="00"/>
                            <tag k="gemeindekennzeichen:kreis" v="00"/>
                            <tag k="gemeindekennzeichen:land" v="00"/>
                            <tag k="gemeindekennzeichen:regierungsbezirk" v="00"/>
                            <tag k="schluesselGesamt" v="00000000"/>
                            <tag k="admin_level" v="8"/>
                        </relation>
                        <relation id="-4" version="0">
                            <member type="relation" role="" ref="-3"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="gemeindekennzeichen:gemeinde" v="00"/>
                            <tag k="gemeindekennzeichen:kreis" v="00"/>
                            <tag k="gemeindekennzeichen:land" v="00"/>
                            <tag k="gemeindekennzeichen:regierungsbezirk" v="00"/>
                            <tag k="object_type" v="AX_Gemeinde"/>
                            <tag k="schluesselGesamt" v="00000000"/>
                        </relation>
                        <relation id="-5" version="0">
                            <member type="way" role="" ref="-14"/>
                            <member type="way" role="" ref="-17"/>
                            <member type="way" role="" ref="-16"/>
                            <member type="way" role="" ref="-15"/>
                            <tag k="object_type" v="AX_KommunalesTeilgebiet"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="hierarchiename" v="Test"/>
                            <tag k="hierarchiestufe" v="1"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="kennzeichen:gemeinde" v="00"/>
                            <tag k="kennzeichen:kreis" v="00"/>
                            <tag k="kennzeichen:land" v="00"/>
                            <tag k="kennzeichen:regierungsbezirk" v="00"/>
                            <tag k="schluesselGesamt" v="00000000"/>
                        </relation>
                        <relation id="-6" version="0">
                            <member type="way" role="" ref="-14"/>
                            <member type="way" role="" ref="-13"/>
                            <member type="way" role="" ref="-12"/>
                            <member type="way" role="" ref="-11"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="object_type" v="AX_KommunalesTeilgebiet"/>
                            <tag k="hierarchiename" v="Test"/>
                            <tag k="hierarchiestufe" v="1"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="kennzeichen:gemeinde" v="00"/>
                            <tag k="kennzeichen:kreis" v="00"/>
                            <tag k="kennzeichen:land" v="00"/>
                            <tag k="kennzeichen:regierungsbezirk" v="00"/>
                            <tag k="schluesselGesamt" v="00000000"/>
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
    void createKommunalesTeilgebietMitGemeindeMitGemeindeteil() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-43" lon="12.318275553002538" lat="49.87290616512915" version="0"/>
                        <node id="-44" lon="12.318275553002538" lat="49.871985174569666" version="0"/>
                        <node id="-47" lon="12.320012791937724" lat="49.871964108148156" version="0"/>
                        <node id="-48" lon="12.319984771954896" lat="49.87290917706879" version="0"/>
                        <node id="-52" lon="12.31910704533641" lat="49.87197509157784" version="0"/>
                        <node id="-68" lon="12.319141057154875" lat="49.8729076902973" version="0"/>
                        <way id="-7" version="0">
                            <nd ref="-43"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-44"/>
                            <nd ref="-52"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-9" version="0">
                            <nd ref="-48"/>
                            <nd ref="-47"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-10" version="0">
                            <nd ref="-43"/>
                            <nd ref="-68"/>
                            <nd ref="-48"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="artDerGebietsgrenze" v="7106"/>
                            <tag k="admin_level" v="8"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-11" version="0">
                            <nd ref="-52"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-12" version="0">
                            <nd ref="-43"/>
                            <nd ref="-44"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-13" version="0">
                            <nd ref="-68"/>
                            <nd ref="-43"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-14" version="0">
                            <nd ref="-52"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-15" version="0">
                            <nd ref="-47"/>
                            <nd ref="-52"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-16" version="0">
                            <nd ref="-47"/>
                            <nd ref="-48"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-17" version="0">
                            <nd ref="-48"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <relation id="-3" version="0">
                            <member type="way" role="" ref="-10"/>
                            <member type="way" role="" ref="-7"/>
                            <member type="way" role="" ref="-8"/>
                            <member type="way" role="" ref="-9"/>
                            <tag k="object_type" v="AX_KommunalesGebiet"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="gemeindekennzeichen:gemeinde" v="00"/>
                            <tag k="gemeindekennzeichen:kreis" v="00"/>
                            <tag k="gemeindekennzeichen:land" v="00"/>
                            <tag k="gemeindekennzeichen:regierungsbezirk" v="00"/>
                            <tag k="gemeindekennzeichen:gemeindeteil" v="00"/>
                            <tag k="schluesselGesamt" v="0000000000"/>
                            <tag k="admin_level" v="8"/>
                        </relation>
                        <relation id="-4" version="0">
                            <member type="relation" role="" ref="-3"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="gemeindekennzeichen:gemeinde" v="00"/>
                            <tag k="gemeindekennzeichen:kreis" v="00"/>
                            <tag k="gemeindekennzeichen:land" v="00"/>
                            <tag k="gemeindekennzeichen:regierungsbezirk" v="00"/>
                            <tag k="gemeindekennzeichen:gemeindeteil" v="00"/>
                            <tag k="schluesselGesamt" v="0000000000"/>
                            <tag k="object_type" v="AX_Gemeinde"/>
                        </relation>
                        <relation id="-5" version="0">
                            <member type="way" role="" ref="-14"/>
                            <member type="way" role="" ref="-17"/>
                            <member type="way" role="" ref="-16"/>
                            <member type="way" role="" ref="-15"/>
                            <tag k="object_type" v="AX_KommunalesTeilgebiet"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="hierarchiename" v="Test"/>
                            <tag k="hierarchiestufe" v="1"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="kennzeichen:gemeinde" v="00"/>
                            <tag k="kennzeichen:kreis" v="00"/>
                            <tag k="kennzeichen:land" v="00"/>
                            <tag k="kennzeichen:regierungsbezirk" v="00"/>
                            <tag k="kennzeichen:gemeindeteil" v="00"/>
                            <tag k="schluesselGesamt" v="0000000000"/>
                        </relation>
                        <relation id="-6" version="0">
                            <member type="way" role="" ref="-14"/>
                            <member type="way" role="" ref="-13"/>
                            <member type="way" role="" ref="-12"/>
                            <member type="way" role="" ref="-11"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="object_type" v="AX_KommunalesTeilgebiet"/>
                            <tag k="hierarchiename" v="Test"/>
                            <tag k="hierarchiestufe" v="1"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="kennzeichen:gemeinde" v="00"/>
                            <tag k="kennzeichen:kreis" v="00"/>
                            <tag k="kennzeichen:land" v="00"/>
                            <tag k="kennzeichen:regierungsbezirk" v="00"/>
                            <tag k="kennzeichen:gemeindeteil" v="00"/>
                            <tag k="schluesselGesamt" v="0000000000"/>
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
                .contains("Gemeindeteil liegt nicht auf Kommunalem Gebiet oder wird darin bereits verwendet.");
    }

    @Test
    void createKommunalesTeilgebietOhneGemeinde() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-43" lon="12.318275553002538" lat="49.87290616512915" version="0"/>
                        <node id="-44" lon="12.318275553002538" lat="49.871985174569666" version="0"/>
                        <node id="-47" lon="12.320012791937724" lat="49.871964108148156" version="0"/>
                        <node id="-48" lon="12.319984771954896" lat="49.87290917706879" version="0"/>
                        <node id="-52" lon="12.31910704533641" lat="49.87197509157784" version="0"/>
                        <node id="-68" lon="12.319141057154875" lat="49.8729076902973" version="0"/>
                        <way id="-14" version="0">
                            <nd ref="-52"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-15" version="0">
                            <nd ref="-47"/>
                            <nd ref="-52"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-16" version="0">
                            <nd ref="-47"/>
                            <nd ref="-48"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <way id="-17" version="0">
                            <nd ref="-48"/>
                            <nd ref="-68"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="artDerGebietsgrenze" v="7107"/>
                            <tag k="boundary" v="administrative"/>
                        </way>
                        <relation id="-5" version="0">
                            <member type="way" role="" ref="-14"/>
                            <member type="way" role="" ref="-17"/>
                            <member type="way" role="" ref="-16"/>
                            <member type="way" role="" ref="-15"/>
                            <tag k="object_type" v="AX_KommunalesTeilgebiet"/>
                            <tag k="admin_level" v="9"/>
                            <tag k="boundary" v="administrative"/>
                            <tag k="hierarchiename" v="Test"/>
                            <tag k="hierarchiestufe" v="1"/>
                            <tag k="bezeichnung" v="Test"/>
                            <tag k="kennzeichen:gemeinde" v="00"/>
                            <tag k="kennzeichen:kreis" v="00"/>
                            <tag k="kennzeichen:land" v="00"/>
                            <tag k="kennzeichen:regierungsbezirk" v="00"/>
                            <tag k="kennzeichen:gemeindeteil" v="00"/>
                            <tag k="schluesselGesamt" v="0000000000"/>
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
                .contains("Gemeindeteil liegt nicht auf Kommunalem Gebiet oder wird darin bereits verwendet.");
    }
}
