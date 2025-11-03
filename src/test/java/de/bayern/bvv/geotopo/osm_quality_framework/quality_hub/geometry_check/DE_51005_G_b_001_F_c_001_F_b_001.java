package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

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
 * 51005 'Leitung' mit BWF 1110 Freileitung hat an einem Knickpunkt stets einen Freileitungsmast.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_51005_G_b_001_F_c_001_F_b_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createAxFreileitungMitFreileitungsmast() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25390' changeset='-1' lat='49.8779648631' lon='12.3250340572'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <node id='-25389' changeset='-1' lat='49.88342915509' lon='12.32507916307'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <node id='-25388' changeset='-1' lat='49.88741072301' lon='12.32604893941'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <way id='-820' changeset='-1'>
                    <nd ref='-25388' />
                    <nd ref='-25389' />
                    <nd ref='-25390' />
                    <tag k='bauwerksfunktion' v='1110' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112224' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222420251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Leitung' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-820' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='node' ref='-25389' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-50' changeset='-1'>
                    <member type='node' ref='-25388' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-40' changeset='-1'>
                    <member type='node' ref='-25390' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-30' changeset='-1'>
                    <member type='node' ref='-25390' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
                  </relation>
                  <relation id='-20' changeset='-1'>
                    <member type='node' ref='-25389' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
                  </relation>
                  <relation id='-10' changeset='-1'>
                    <member type='node' ref='-25388' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
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
    void createAxFreileitungMitUmspannstation() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25418' changeset='-1' lat='49.87797377927' lon='12.3221490943' />
                  <node id='-25417' changeset='-1' lat='49.87793275088' lon='12.32838849564' />
                  <node id='-25416' changeset='-1' lat='49.87625055697' lon='12.32838849564' />
                  <node id='-25415' changeset='-1' lat='49.87639416118' lon='12.32192625854' />
                  <node id='-25390' changeset='-1' lat='49.8779648631' lon='12.3250340572' />
                  <node id='-25389' changeset='-1' lat='49.88342915509' lon='12.32507916307'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <node id='-25388' changeset='-1' lat='49.88741072301' lon='12.32604893941'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <way id='-820' changeset='-1'>
                    <nd ref='-25388' />
                    <nd ref='-25389' />
                    <nd ref='-25390' />
                    <tag k='bauwerksfunktion' v='1110' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112224' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222420251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Leitung' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-820' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='node' ref='-25389' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-50' changeset='-1'>
                    <member type='node' ref='-25388' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-30' changeset='-1'>
                    <member type='node' ref='-25390' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
                  </relation>
                  <relation id='-20' changeset='-1'>
                    <member type='node' ref='-25389' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
                  </relation>
                  <relation id='-10' changeset='-1'>
                    <member type='node' ref='-25388' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
                  </relation>
                  <way id='-837' changeset='-1'>
                    <nd ref='-25415' />
                    <nd ref='-25416' />
                    <nd ref='-25417' />
                    <nd ref='-25390' />
                    <nd ref='-25418' />
                    <nd ref='-25415' />
                    <tag k='funktion' v='2540' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112225' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222520251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_IndustrieUndGewerbeflaeche' />
                  </way>
                  <relation id='-84' changeset='-1'>
                    <member type='way' ref='-837' role='' />
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
    void createAxFreileitungOhneFreileitungsmast() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25390' changeset='-1' lat='49.8779648631' lon='12.3250340572'>
                    <tag k='bauwerksfunktion' v='1251' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112223' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerIndustrieUndGewerbe' />
                  </node>
                  <node id='-25389' changeset='-1' lat='49.88342915509' lon='12.32507916307' />
                  <node id='-25388' changeset='-1' lat='49.88741072301' lon='12.32604893941' />
                  <way id='-820' changeset='-1'>
                    <nd ref='-25388' />
                    <nd ref='-25389' />
                    <nd ref='-25390' />
                    <tag k='bauwerksfunktion' v='1110' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11112224' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111222420251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_Leitung' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-820' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='node' ref='-25389' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-20' changeset='-1'>
                    <member type='node' ref='-25390' role='' />
                    <tag k='hoehe' v='10' />
                    <tag k='object_type' v='AX_objekthoehe' />
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
                .contains("Ein Objekt 'AX_Leitung' muss an einen Knickpunkt stets einen Freileitungsmast haben und an einen Freileitungsmast, der Landesgrenze oder in einem Kraftwerk oder einer Umspannstation beginnen und enden.");
    }
}