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
 * Die Attributart 'Bauwerksfunktion' mit der Werteart 1480 Schießanlage
 * darf nur innerhalb 41008 Sport Freizeit Erholung mit FKT 4100 Sportanlage,
 * 41007 FlaecheBesondererFunktionalerPraegung ohne FKT oder mit FKT 1170 Sicherheit und
 * Ordnung oder beliebiger TatsächlicherNutzung bei Überlagerung von 71011 Sonstiges Recht mit
 * ADF 4720 Truppen-, Standortübungsplatz liegen.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfiguration.class)
class DE_51006_G_b_005_F_b_006 extends DatabaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createSchiessanlageAufTruppenuebungsplatz() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
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
                  <way id='-809' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                    <tag k='bauwerksfunktion' v='1480' />
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
                    <tag k='object_type' v='AX_SonstigesRecht' />
                    <tag k='artDerFestlegung' v='4720' />
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
    void createSchiessanlageAufFlaecheBesondererFunktionalerPraegungSicherheit() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
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
                  <way id='-809' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                    <tag k='bauwerksfunktion' v='1480' />
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
                    <tag k='object_type' v='AX_FlaecheBesondererFunktionalerPraegung' />
                    <tag k='funktion' v='1170' />
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
    void createGradierwerkAufFlaecheBesondererFunktionalerPraegungOhneFunktion() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
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
                  <way id='-809' changeset='-1'>
                    <nd ref='-25473' />
                    <nd ref='-25474' />
                    <nd ref='-25475' />
                    <nd ref='-25476' />
                    <nd ref='-25473' />
                    <tag k='bauwerksfunktion' v='1480' />
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
                    <tag k='object_type' v='AX_FlaecheBesondererFunktionalerPraegung' />
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
    void createSchiessanlageAufSafaripark() throws Exception {
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
                    <tag k='bauwerksfunktion' v='1480' />
                    <tag k='identifikator:UUID' v='DEBYBDLM33333333' />
                    <tag k='identifikator:UUIDundZeit' v='DEBYBDLM3333333320251014T125300Z' />
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
                    <tag k='funktion' v='4220' />
                  </way>
                  <relation id='-50' changeset='-1'>
                    <member type='way' ref='-741' role='' />
                    <tag k='advStandardModell' v='Basis-DLM' />
                    <tag k='object_type' v='AA_modellart' />
                  </relation>
                  <relation id='-70' changeset='-1'>
                    <member type='way' ref='-903' role='' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1480 muss ein Objekt 'AX_FlaecheBesondererFunktionalerPraegung' ohne 'funktion' oder mit 'funktion' 1170 oder 'AX_SportFreizeitUndErholungsflaeche' mit der 'funktion' 4100 oder 'AX_SonstigesRecht' mit 'artDerFestlegung' 4720 überlagern.");
    }

    @Test
    void createSchiessanlageOhneDarunterliegenderFlaeche() throws Exception {
        // Arrange
        final Long CHANGESET_ID = 1L;
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="JOSM">
                <create>
                  <node id='-25479' changeset='-1' lat='49.8815270855' lon='12.32345332969' />
                  <node id='-25478' changeset='-1' lat='49.88153975261' lon='12.32642548413' />
                  <node id='-25477' changeset='-1' lat='49.8799252856' lon='12.3234923098' />
                  <node id='-25470' changeset='-1' lat='49.87994713968' lon='12.32645981108' />
                  <way id='-903' changeset='-1'>
                    <nd ref='-25477' />
                    <nd ref='-25470' />
                    <nd ref='-25478' />
                    <nd ref='-25479' />
                    <nd ref='-25477' />
                    <tag k='bauwerksfunktion' v='1480' />
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1480 muss ein Objekt 'AX_FlaecheBesondererFunktionalerPraegung' ohne 'funktion' oder mit 'funktion' 1170 oder 'AX_SportFreizeitUndErholungsflaeche' mit der 'funktion' 4100 oder 'AX_SonstigesRecht' mit 'artDerFestlegung' 4720 überlagern.");
    }
}
