package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.attribute_check;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.config.JacksonConfiguration;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AdV-Beschreibung:
 * Der Wert von 'objekthoehe'.'hoehe' muss größer Null sein.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_52003_B_c_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createSchleuseMitGueltigerHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                    <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                    <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                    <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-18"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <nd ref="-19"/>
                        <nd ref="-8"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Schiffsverkehr"/>
                        <tag k="funktion" v="5620"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Schleuse' />
                    </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-2' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='50' />
                  </relation>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-4' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createSchleuseOhneHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                    <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                    <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                    <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-18"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <nd ref="-19"/>
                        <nd ref="-8"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Schiffsverkehr"/>
                        <tag k="funktion" v="5620"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Schleuse' />
                    </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-4' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        QualityHubResultDto qualityHubResultDto = this.objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), QualityHubResultDto.class);

        // Assert
        assertThat(qualityHubResultDto).as("Quality-Hub result must not be null").isNotNull();
        assertThat(qualityHubResultDto.isValid()).withFailMessage("Expected the result to be valid, but it was invalid.").isTrue();
    }

    @Test
    void createSchleuseMitUngueltigerHoehe() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                    <node id="-8" lon="12.330094720996826" lat="49.88193386812974" version="0"/>
                    <node id="-11" lon="12.330085381002549" lat="49.88104314212553" version="0"/>
                    <node id="-18" lon="12.329216761534958" lat="49.881049160599645" version="0"/>
                    <node id="-19" lon="12.329258791509199" lat="49.88196095075758" version="0"/>
                    <node id="-30" lon="12.329237982349571" lat="49.881509523012845" version="0"/>
                    <node id="-31" lon="12.330090145657472" lat="49.881497534397276" version="0"/>
                    <node id="-32" lon="12.330088788682724" lat="49.88136812381623" version="0"/>
                    <node id="-33" lon="12.329232711793546" lat="49.88139518446176" version="0"/>
                    <way id="-4" version="0">
                        <nd ref="-11"/>
                        <nd ref="-18"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <nd ref="-19"/>
                        <nd ref="-8"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-11"/>
                        <tag k="object_type" v="AX_Schiffsverkehr"/>
                        <tag k="funktion" v="5620"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    </way>
                    <way id="-6" version="0">
                        <nd ref="-30"/>
                        <nd ref="-31"/>
                        <nd ref="-32"/>
                        <nd ref="-33"/>
                        <nd ref="-30"/>
                        <tag k='identifikator:UUID' v='DEBYBDLM12345678' />
                        <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1234567820251014T125300Z' />
                        <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                        <tag k='object_type' v='AX_Schleuse' />
                    </way>
                  <relation id='-1' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-2' changeset='-1'>
                    <member type='way' ref='-6' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='0' />
                  </relation>
                  <relation id='-3' changeset='-1'>
                    <member type='way' ref='-4' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                </create>
                </osmChange>
                """;

        // Act
        MvcResult mvcResult = this.mockMvc.perform(
                        post("/osm-quality-framework/v1/quality-hub/check/changeset/{id}", CHANGESET_ID)
                                .contentType(MediaType.APPLICATION_XML)
                                .content(CHANGESET_XML))
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
                .contains("Das Tag 'hoehe' der Relation 'AX_objekthoehe' muss größer Null sein.");
    }
}