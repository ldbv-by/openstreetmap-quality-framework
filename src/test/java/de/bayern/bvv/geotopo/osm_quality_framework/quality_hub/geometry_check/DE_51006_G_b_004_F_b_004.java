package de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.geometry_check;

import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.config.JtsJackson3Module;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_hub.dto.QualityHubResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceErrorDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_services.dto.QualityServiceResultDto;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Die Werteart 1490 der Attributart 'Bauwerksfunktion' überlagert immer ein
 * Objekt 41007 'FlächeBesondererFunktionalerPrägung' ohne FKT oder mit FKT 1150 'Gesundheit, Kur' oder
 * 41008 'SportFreizeitUndErholungsfläche' mit 'Funktion' 4200 'Freizeitanlage', 4400 'Grünanlage' oder 4420 'Park'.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_51006_G_b_004_F_b_004 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("geometry-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.51006.G.b.004_F.b.004"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createGradierwerkAufGruenanlage() throws Exception {
        // Arrange
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
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1490' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='object_type' v='AX_SportFreizeitUndErholungsflaeche' />
                    <tag k='funktion' v='4400' />
                  </way>
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
    void createGradierwerkAufFlaecheBesondererFunktionalerPraegungKur() throws Exception {
        // Arrange
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
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1490' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='object_type' v='AX_FlaecheBesondererFunktionalerPraegung' />
                    <tag k='funktion' v='1150' />
                  </way>
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
    void createGradierwerkAufFlaecheBesondererFunktionalerPraegungOhneFunktion() throws Exception {
        // Arrange
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
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1490' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='object_type' v='AX_FlaecheBesondererFunktionalerPraegung' />
                  </way>
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
    void createGradierwerkAufSafaripark() throws Exception {
        // Arrange
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
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1490' />
                  </way>
                  <way id='-741' changeset='-1'>
                    <nd ref='-25468' />
                    <nd ref='-25469' />
                    <nd ref='-25478' />
                    <nd ref='-25470' />
                    <nd ref='-25477' />
                    <nd ref='-25471' />
                    <nd ref='-25468' />
                    <tag k='object_type' v='AX_SportFreizeitUndErholungsflaeche' />
                    <tag k='funktion' v='4220' />
                  </way>
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1490 muss ein Objekt 'AX_FlaecheBesondererFunktionalerPraegung' ohne 'funktion' oder mit 'funktion' 1150 oder 'AX_SportFreizeitUndErholungsflaeche' mit der 'funktion' 4200, 4400, oder 4420 überlagern.");
    }

    @Test
    void createGradierwerkOhneDarunterliegenderFlaeche() throws Exception {
        // Arrange
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
                    <tag k='object_type' v='AX_BauwerkOderAnlageFuerSportFreizeitUndErholung' />
                    <tag k='bauwerksfunktion' v='1490' />
                  </way>
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
                .contains("Ein Objekt mit der 'bauwerksfunktion' 1490 muss ein Objekt 'AX_FlaecheBesondererFunktionalerPraegung' ohne 'funktion' oder mit 'funktion' 1150 oder 'AX_SportFreizeitUndErholungsflaeche' mit der 'funktion' 4200, 4400, oder 4420 überlagern.");
    }
}
