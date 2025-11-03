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
 * Die Wertearten 1431 und 1432 der Attributart 'Bauwerksfunktion'
 * überlagern immer Objekt(e) 41007 'FlächeBesonderer FunktionalerPrägung' oder
 * Objekt(e) 41008 'SportFreizeitUndErholungsfläche'.
 *
 * Die Wertearten 1430-1432 der Attributart 'Bauwerksfunktion'
 * überlagern immer entweder Objekt(e) 41007 'FlächeBesonderer FunktionalerPrägung' oder
 * Objekt(e) 41008 'SportFreizeitUndErholungsfläche'.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_51006_G_b_001_F_b_001 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createZuschauertribueneAufSportFreizeitUndErholung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25479' changeset='-1' lat='49.8815270855' lon='12.32345332969' />
                  <node id='-25478' changeset='-1' lat='49.88153975261' lon='12.32642548413' />
                  <node id='-25477' changeset='-1' lat='49.8799252856' lon='12.3234923098' />
                  <node id='-25476' changeset='-1' lat='49.88647982036' lon='12.32394954016' />
                  <node id='-25475' changeset='-1' lat='49.88546423895' lon='12.32398845939' />
                  <node id='-25474' changeset='-1' lat='49.88543916259' lon='12.320972219' />
                  <node id='-25473' changeset='-1' lat='49.88642966869' lon='12.32095275939' />
                  <node id='-25471' changeset='-1' lat='49.87989698123' lon='12.3196489457' />
                  <node id='-25470' changeset='-1' lat='49.87994713968' lon='12.32645981108' />
                  <node id='-25469' changeset='-1' lat='49.88716941303' lon='12.32630413416' />
                  <node id='-25468' changeset='-1' lat='49.88711926208' lon='12.31949326877' />
                  <way id='-903' changeset='-1'>
                    <nd ref='-25477' />
                    <nd ref='-25470' />
                    <nd ref='-25478' />
                    <nd ref='-25479' />
                    <nd ref='-25477' />
                    <tag k='bauwerksfunktion' v='1432' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                  </way>
                  <way id='-809' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                    <tag k='bauwerksfunktion' v='1431' />
                    <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_SportFreizeitUndErholungsflaeche' />
                  </way>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-741' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-809' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-809' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='10' />
                  </relation>
                  <relation id='-90' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='10' />
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
    void createZuschauertribueneOhneSportFreizeitUndErholung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25477' changeset='-1' lat='49.8799252856' lon='12.3234923098' />
                  <node id='-25470' changeset='-1' lat='49.87994713968' lon='12.32645981108' />
                  <node id='-25478' changeset='-1' lat='49.88153975261' lon='12.32642548413' />
                  <node id='-25479' changeset='-1' lat='49.8815270855' lon='12.32345332969' />
                  <way id='-903' changeset='-1'>
                    <nd ref='-25477' />
                    <nd ref='-25470' />
                    <nd ref='-25478' />
                    <nd ref='-25479' />
                    <nd ref='-25477' />
                    <tag k='bauwerksfunktion' v='1432' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                  </way>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='10' />
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
                .contains("Die Wertearten mit der 'bauwerksfunktion' 1430, 1431 und 1432 müssen 'AX_FlaecheBesondererFunktionalerPraegung' oder 'AX_SportFreizeitUndErholungsflaeche' überlagern.");
    }

    @Test
    void createZuschauertribueneSchneidetSportFreizeitUndErholung() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25484' changeset='-1' lat='49.88772107004' lon='12.327627388' />
                  <node id='-25483' changeset='-1' lat='49.8861914601' lon='12.32758846877' />
                  <node id='-25482' changeset='-1' lat='49.88626668795' lon='12.32517547646' />
                  <node id='-25481' changeset='-1' lat='49.88752046822' lon='12.32525331493' />
                  <node id='-25478' changeset='-1' lat='49.88153975261' lon='12.32642548413' />
                  <node id='-25477' changeset='-1' lat='49.8799252856' lon='12.3234923098' />
                  <node id='-25476' changeset='-1' lat='49.88647982036' lon='12.32394954016' />
                  <node id='-25475' changeset='-1' lat='49.88546423895' lon='12.32398845939' />
                  <node id='-25474' changeset='-1' lat='49.88543916259' lon='12.320972219' />
                  <node id='-25473' changeset='-1' lat='49.88642966869' lon='12.32095275939' />
                  <node id='-25471' changeset='-1' lat='49.87989698123' lon='12.3196489457' />
                  <node id='-25470' changeset='-1' lat='49.87994713968' lon='12.32645981108' />
                  <node id='-25469' changeset='-1' lat='49.88716941303' lon='12.32630413416' />
                  <node id='-25468' changeset='-1' lat='49.88711926208' lon='12.31949326877' />
                  <way id='-903' changeset='-1'>
                    <nd ref='-25481' />
                    <nd ref='-25482' />
                    <nd ref='-25483' />
                    <nd ref='-25484' />
                    <nd ref='-25481' />
                    <tag k='bauwerksfunktion' v='1432' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                  </way>
                  <way id='-809' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                    <tag k='bauwerksfunktion' v='1431' />
                    <tag k='identifikator:UUID' v='DEBYBDLM22222222' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM2222222220251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='identifikator:UUID' v='DEBYBDLM11111111' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM1111111120251014T125300Z' />
                    <tag k='lebenszeitintervall:beginnt' v='2025-10-14T12:53:00Z' />
                    <tag k='object_type' v='AX_SportFreizeitUndErholungsflaeche' />
                  </way>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-741' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-60' changeset='-1'>
                    <member type='way' ref='-809' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-80' changeset='-1'>
                    <member type='way' ref='-809' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='10' />
                  </relation>
                  <relation id='-90' changeset='-1'>
                    <member type='way' ref='-903' role='' />
                    <tag k='object_type' v='AX_objekthoehe' />
                    <tag k='hoehe' v='10' />
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
                .contains("Die Wertearten mit der 'bauwerksfunktion' 1430, 1431 und 1432 müssen 'AX_FlaecheBesondererFunktionalerPraegung' oder 'AX_SportFreizeitUndErholungsflaeche' überlagern.");
    }
}
