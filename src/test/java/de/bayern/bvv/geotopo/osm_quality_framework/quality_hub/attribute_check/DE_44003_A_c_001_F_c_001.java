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
 * Die zu einem ZUSO 44003 'Kanal' gehörenden REO 44004 'Gewässerachse' führen bei der Attributart 'Fließrichtung' immer den Wert 'FALSE'.
 * <p>
 * Innerhalb der Längenausdehnung der zu einem ZUSO 44003 AX_Kanal gehörenden REO 44004 AX_Gewaesserachse und 44001 AX_Fliessgewaesser ist die Fließrichtung an allen Gewässerachsen und Gewässerstationierungsachsen (=linienförmiger Repräsentant von Fließgewässer) FALSE.
 * Von dieser Prüfung sind Gewässerstationierungsachsen mit AGA 3001 ausgenommen, da dort auch Fließrichtung TRUE erlaubt ist.
 * Besitzt ein zu diesem ZUSO 44003 AX_Kanal gehörendes REO 44001 AX_Fliessgewaesser eine hDU-Relation zu einem Bauwerk, muss ein REO 57003 AX_Gewaesserstationierungsachse mit der gleichen GWK eine hDU-Relation zu diesem Bauwerk tragen.
 * Die Fließrichtung muss FALSE sein. Hinweis: Kreuzt eine Gewässerstationierungsachse mit einer anderen GWK dieses ZUSO Kanal und hat eine hDU-Relation zu einem Bauwerk ist diese Gewässerstationierungsachse in der Längenausdehnung nicht zu betrachten.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_44003_A_c_001_F_c_001 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.44003.A.c.001_F.c.001"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createKanalMitGewaesserachseFliessrichtungFALSE() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-4" lon="12.331021236757456" lat="49.87835809096834" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-9" lon="12.331024850360901" lat="49.87850479802609" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='funktion' v='8300' />
                        <tag k='fliessrichtung' v='FALSE' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-9"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='funktion' v='8300' />
                        <tag k='fliessrichtung' v='FALSE' />
                    </way>
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <member type='way' ref='-2' role='' />
                        <tag k="object_type" v="AX_Kanal"/>
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
    void createKanalMitGewaesserachseMitFliessrichtungTRUE() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                <create>
                    <node id="-1" lon="12.33103749863201" lat="49.87911888618271" version="0"/>
                    <node id="-2" lon="12.331030993733307" lat="49.878754209010715" version="0"/>
                    <node id="-4" lon="12.331021236757456" lat="49.87835809096834" version="0"/>
                    <node id="-7" lon="12.33103432094183" lat="49.87894073908409" version="0"/>
                    <node id="-9" lon="12.331024850360901" lat="49.87850479802609" version="0"/>
                    <way id="-1" version="0">
                        <nd ref="-1"/>
                        <nd ref="-7"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='funktion' v='8300' />
                        <tag k='fliessrichtung' v='TRUE' />
                    </way>
                    <way id="-2" version="0">
                        <nd ref="-4"/>
                        <nd ref="-9"/>
                        <nd ref="-2"/>
                        <tag k="object_type" v="AX_Gewaesserachse"/>
                        <tag k='breiteDesGewaessers' v='12' />
                        <tag k='funktion' v='8300' />
                        <tag k='fliessrichtung' v='TRUE' />
                        <tag k='hydrologischesMerkmal' v='2000' />
                    </way>
                    <relation id='-3' changeset='-1'>
                        <member type='way' ref='-1' role='' />
                        <member type='way' ref='-2' role='' />
                        <tag k="object_type" v="AX_Kanal"/>
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
                .contains("Die zu 'AX_Kanal' gehörenden 'AX_Gewaesserachse' dürfen keine Fliessrichtung haben.");
    }

    @Test
    void createKanalMitGewaesserstationierungsachseMitFliessrichtungFALSE() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
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
    void createKanalMitGewaesserstationierungsachseMitFliessrichtungTRUE() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="2000"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
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
                .contains("Die zu 'AX_Kanal' gehörenden 'AX_Gewaesserachse' dürfen keine Fliessrichtung haben.");
    }

    @Test
    void createKanalMitGewaesserstationierungsachse3001MitFliessrichtungTRUE() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="3001"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
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
    void createKanalMitFliessgewaesserMitHDUUndIdentischerGewaesserkennzahl() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="3001"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                                <tag k="gewaesserkennzahl" v="1234"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <way id="-4" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                                <tag k="bauwerksfunktion" v="1800"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
                                <tag k="gewaesserkennzahl" v="1234"/>
                            </relation>
                            <relation id="-2" version="0">
                                <member type="way" role="under" ref="-4"/>
                                <member type="way" role="over" ref="-1"/>
                                <member type="way" role="over" ref="-2"/>
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
    void createKanalMitFliessgewaesserMitHDUUndUnterschiedlicherGewaesserkennzahl() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="3001"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                                <tag k="gewaesserkennzahl" v="1234"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <way id="-4" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                                <tag k="bauwerksfunktion" v="1800"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
                                <tag k="gewaesserkennzahl" v="9999"/>
                            </relation>
                            <relation id="-2" version="0">
                                <member type="way" role="under" ref="-4"/>
                                <member type="way" role="over" ref="-1"/>
                                <member type="way" role="over" ref="-2"/>
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
                .contains("Die zu 'AX_Kanal' gehörenden 'AX_Gewaesserachse' dürfen keine Fliessrichtung haben.");
    }

    @Test
    void createKanalMitFliessgewaesserMitHDUUndIdentischerGewaesserkennzahlOhneHDU() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                    <osmChange version="0.6" generator="iD">
                        <create>
                            <node id="-1" lon="12.313699851245289" lat="49.8735977168509" version="0"/>
                            <node id="-2" lon="12.315748847439203" lat="49.87361867762345" version="0"/>
                            <node id="-3" lon="12.315765109313757" lat="49.87252870538675" version="0"/>
                            <node id="-4" lon="12.313699851245289" lat="49.87252870538675" version="0"/>
                            <node id="-7" lon="12.313699851245289" lat="49.87306321407673" version="0"/>
                            <node id="-8" lon="12.315756979128162" lat="49.87307364419778" version="0"/>
                            <node id="-10" lon="12.31727746364736" lat="49.873084175081274" version="0"/>
                            <way id="-1" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_Fliessgewaesser"/>
                            </way>
                            <way id="-2" version="0">
                                <nd ref="-7"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserstationierungsachse"/>
                                <tag k="artDerGewaesserstationierungsachse" v="3001"/>
                                <tag k="fliessrichtung" v="TRUE"/>
                                <tag k="gewaesserkennzahl" v="1234"/>
                            </way>
                            <way id="-3" version="0">
                                <nd ref="-10"/>
                                <nd ref="-8"/>
                                <tag k="object_type" v="AX_Gewaesserachse"/>
                                <tag k='funktion' v='8300' />
                                <tag k="breiteDesGewaessers" v="12"/>
                                <tag k="fliessrichtung" v="FALSE"/>
                            </way>
                            <way id="-4" version="0">
                                <nd ref="-1"/>
                                <nd ref="-2"/>
                                <nd ref="-8"/>
                                <nd ref="-3"/>
                                <nd ref="-4"/>
                                <nd ref="-7"/>
                                <nd ref="-1"/>
                                <tag k="object_type" v="AX_BauwerkImVerkehrsbereich"/>
                                <tag k="bauwerksfunktion" v="1800"/>
                            </way>
                            <relation id="-1" version="0">
                                <member type="way" role="" ref="-3"/>
                                <member type="way" role="" ref="-1"/>
                                <tag k="object_type" v="AX_Kanal"/>
                                <tag k="gewaesserkennzahl" v="1234"/>
                            </relation>
                            <relation id="-2" version="0">
                                <member type="way" role="under" ref="-4"/>
                                <member type="way" role="over" ref="-1"/>
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
                .contains("Die zu 'AX_Kanal' gehörenden 'AX_Gewaesserachse' dürfen keine Fliessrichtung haben.");
    }
}