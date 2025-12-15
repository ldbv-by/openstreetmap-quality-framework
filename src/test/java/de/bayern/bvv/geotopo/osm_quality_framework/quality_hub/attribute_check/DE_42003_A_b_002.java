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
 * Wenn bei einem Objekt 42003 der Attributart 'BVB' die Werteart 1000 'Überörtlicher Durchgangsverkehr' belegt ist, schließt geometrisch beidseitig immer ein weiteres Objekt 42003 mit BVB 1000 an.
 * <p>
 * Wenn dabei ein REO 42003 AX_Strassenachse mit Attributbelegung BVB 1000 an ein REO 42005 AX_Fahrbahnachse geometrisch anschließt das zu einem ZUSO AX_Strasse mit einem REO AX_Strassenachse gehört
 * das die Attributbelegung BVB 1000 führt, wird keine Fehlermeldung erzeugt.
 * <p>
 * Ein Objekt 42003 mit BVB 1000 das an einem Objekt 57002 AX_SchifffahrtslinieFaehrverkehr ART 1710 Autofährverkehr oder 75009 Gebietsgrenze mit AGZ 7102 Landesgrenze endet,
 * schließt geometrisch nur einseitig an einem weiteren Objekt 42003 mit BVB 1000 an.
 * <p>
 * Ist das zu untersuchende Objekt 42003 mit BVB 1000 Bestandteil eines ZUSO 42002 AX_Strasse mit FTR 2000, bei dem an den zum ZUSO gehörenden Objekten 42005 AX_Fahrbahnachse
 * jeweils 1. an mindestens einem Ende ein Objekt 42003 AX_Strassenachse mit BVB 1000 anschließt, dessen Objektidentifikator unterschiedlich zu dem zu untersuchenden Objekt ist und
 * das Bestandteil eines ZUSO 42002 AX_Strasse ohne FTR 2000 ist oder 2. an beiden Enden weitere Objekte 42005 AX_Fahrbahnachse anschließen, ist ebenfalls keine Fehlermeldung auszugeben.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DE_42003_A_b_002 extends DatabaseIntegrationTest {

    final Long CHANGESET_ID = 1L;

    Set<String> stepsToValidate = new HashSet<>(Set.of("attribute-check", "object-number-assignment"));
    Set<String> rulesToValidate = new HashSet<>(Set.of("DE.42003.A.b.002"));

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .addModule(new JtsJackson3Module())
            .build();

    @Test
    void createDurchgehendesStrassennetz() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-24" lon="12.323757945938743" lat="49.87711678845477" version="0"/>
                        <node id="-25" lon="12.327306160503195" lat="49.8771222262015" version="0"/>
                        <node id="-27" lon="12.321831953134142" lat="49.87709775633636" version="0"/>
                        <node id="-36" lon="12.328696334937192" lat="49.87712494507473" version="0"/>
                        <node id="-37" lon="12.325177390213936" lat="49.878755931315" version="0"/>
                        <node id="-39" lon="12.325177390213936" lat="49.875656421226275" version="0"/>
                        <node id="-42" lon="12.32622312536539" lat="49.87827125209103" version="0"/>
                        <way id="-5" version="0">
                            <nd ref="-24"/>
                            <nd ref="-25"/>
                            <tag k="besondereVerkehrsbedeutung" v="1000"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-27"/>
                            <nd ref="-24"/>
                            <tag k="besondereVerkehrsbedeutung" v="1000"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-25"/>
                            <nd ref="-36"/>
                            <tag k="besondereVerkehrsbedeutung" v="1000"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-27"/>
                            <nd ref="-37"/>
                            <nd ref="-42"/>
                            <nd ref="-36"/>
                            <nd ref="-39"/>
                            <nd ref="-27"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                        </way>
                        <relation id='-1' changeset='-1'>
                            <member type='way' ref='-5' role='' />
                            <member type='way' ref='-6' role='' />
                            <member type='way' ref='-7' role='' />
                            <tag k='widmung' v='1301' />
                            <tag k='object_type' v='AX_Strasse' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-8' role='outer' />
                            <tag k='object_type' v='AX_KommunalesGebiet' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                            <tag k='schluesselGesamt' v='0000000000' />
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='relation' ref='-2' role='' />
                            <tag k='object_type' v='AX_Gemeinde' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='bezeichnung' v='Test' />
                            <tag k='schluesselGesamt' v='00000000' />
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
    void createStrassennetzMitUnterschiedlicherVerkehrsbedeutung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                    <create>
                        <node id="-24" lon="12.323757945938743" lat="49.87711678845477" version="0"/>
                        <node id="-25" lon="12.327306160503195" lat="49.8771222262015" version="0"/>
                        <node id="-27" lon="12.321831953134142" lat="49.87709775633636" version="0"/>
                        <node id="-36" lon="12.328696334937192" lat="49.87712494507473" version="0"/>
                        <node id="-37" lon="12.325177390213936" lat="49.878755931315" version="0"/>
                        <node id="-39" lon="12.325177390213936" lat="49.875656421226275" version="0"/>
                        <node id="-42" lon="12.32622312536539" lat="49.87827125209103" version="0"/>
                        <way id="-5" version="0">
                            <nd ref="-24"/>
                            <nd ref="-25"/>
                            <tag k="besondereVerkehrsbedeutung" v="1000"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-6" version="0">
                            <nd ref="-27"/>
                            <nd ref="-24"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-7" version="0">
                            <nd ref="-25"/>
                            <nd ref="-36"/>
                            <tag k="besondereVerkehrsbedeutung" v="1000"/>
                            <tag k="object_type" v="AX_Strassenachse"/>
                        </way>
                        <way id="-8" version="0">
                            <nd ref="-27"/>
                            <nd ref="-37"/>
                            <nd ref="-42"/>
                            <nd ref="-36"/>
                            <nd ref="-39"/>
                            <nd ref="-27"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                        </way>
                        <relation id='-1' changeset='-1'>
                            <member type='way' ref='-5' role='' />
                            <member type='way' ref='-6' role='' />
                            <member type='way' ref='-7' role='' />
                            <tag k='widmung' v='1301' />
                            <tag k='object_type' v='AX_Strasse' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-8' role='outer' />
                            <tag k='object_type' v='AX_KommunalesGebiet' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                            <tag k='schluesselGesamt' v='0000000000' />
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='relation' ref='-2' role='' />
                            <tag k='object_type' v='AX_Gemeinde' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='bezeichnung' v='Test' />
                            <tag k='schluesselGesamt' v='00000000' />
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
                .contains("Das Netz des überörtlichen Durchgangsverkehrs BVB 1000 ist unterbrochen.");
    }

    @Test
    void createDurchgehendesStrassennetzMitFahrbahntrennung() throws Exception {
        // Arrange
        final String CHANGESET_XML = """
                <osmChange version="0.6" generator="iD">
                	<create>
                		<node id="-24" lon="12.324883180667154" lat="49.87714165382029" version="0"/>
                		<node id="-25" lon="12.327306160503195" lat="49.8771222262015" version="0"/>
                		<node id="-27" lon="12.321831953134142" lat="49.87709775633636" version="0"/>
                		<node id="-36" lon="12.328696334937192" lat="49.87712494507473" version="0"/>
                		<node id="-37" lon="12.325177390213936" lat="49.878755931315" version="0"/>
                		<node id="-39" lon="12.325177390213936" lat="49.875656421226275" version="0"/>
                		<node id="-42" lon="12.32622312536539" lat="49.87827125209103" version="0"/>
                		<node id="-107" lon="12.32542105636447" lat="49.87700880186305" version="0"/>
                		<node id="-108" lon="12.326900175253856" lat="49.877326357790075" version="0"/>
                		<node id="-110" lon="12.326799108687581" lat="49.877010415892244" version="0"/>
                		<node id="-116" lon="12.325468601064292" lat="49.87728713647031" version="0"/>
                		<node id="-123" lon="12.325454698137998" lat="49.877149140700716" version="0"/>
                		<node id="-135" lon="12.326720019960796" lat="49.87716681113128" version="0"/>
                		<way id="-6" version="0">
                			<nd ref="-27"/>
                			<nd ref="-24"/>
                			<tag k="besondereVerkehrsbedeutung" v="1000"/>
                			<tag k="object_type" v="AX_Strassenachse"/>
                		</way>
                		<way id="-7" version="0">
                			<nd ref="-25"/>
                			<nd ref="-36"/>
                			<tag k="besondereVerkehrsbedeutung" v="1000"/>
                			<tag k="object_type" v="AX_Strassenachse"/>
                		</way>
                		<way id="-14" version="0">
                			<nd ref="-24"/>
                			<nd ref="-107"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-15" version="0">
                			<nd ref="-108"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-16" version="0">
                			<nd ref="-110"/>
                			<nd ref="-25"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-17" version="0">
                			<nd ref="-24"/>
                			<nd ref="-116"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-18" version="0">
                			<nd ref="-116"/>
                			<nd ref="-108"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-19" version="0">
                			<nd ref="-107"/>
                			<nd ref="-110"/>
                			<tag k="object_type" v="AX_Fahrbahnachse"/>
                		</way>
                		<way id="-20" version="0">
                			<nd ref="-24"/>
                			<nd ref="-123"/>
                			<tag k="besondereVerkehrsbedeutung" v="1000"/>
                			<tag k="object_type" v="AX_Strassenachse"/>
                		</way>
                		<way id="-21" version="0">
                			<nd ref="-123"/>
                			<nd ref="-135"/>
                			<tag k="besondereVerkehrsbedeutung" v="1000"/>
                			<tag k="object_type" v="AX_Strassenachse"/>
                		</way>
                		<way id="-22" version="0">
                			<nd ref="-135"/>
                			<nd ref="-25"/>
                			<tag k="besondereVerkehrsbedeutung" v="1000"/>
                			<tag k="object_type" v="AX_Strassenachse"/>
                		</way>
                		<relation id='-1' changeset='-1'>
                            <member type='way' ref='-14' role='' />
                            <member type='way' ref='-15' role='' />
                            <member type='way' ref='-16' role='' />
                            <member type='way' ref='-17' role='' />
                            <member type='way' ref='-18' role='' />
                            <member type='way' ref='-19' role='' />
                            <member type='way' ref='-20' role='' />
                            <member type='way' ref='-21' role='' />
                            <member type='way' ref='-22' role='' />
                            <tag k='widmung' v='1301' />
                            <tag k='object_type' v='AX_Strasse' />
                            <tag k='fahrbahntrennung' v='2000' />
                        </relation>
                        <relation id='-2' changeset='-1'>
                            <member type='way' ref='-6' role='' />
                            <tag k='widmung' v='1301' />
                            <tag k='object_type' v='AX_Strasse' />
                        </relation>
                        <relation id='-3' changeset='-1'>
                            <member type='way' ref='-7' role='' />
                            <tag k='widmung' v='1301' />
                            <tag k='object_type' v='AX_Strasse' />
                        </relation>
                		<way id="-8" version="0">
                            <nd ref="-27"/>
                            <nd ref="-37"/>
                            <nd ref="-42"/>
                            <nd ref="-36"/>
                            <nd ref="-39"/>
                            <nd ref="-27"/>
                            <tag k="artDerGebietsgrenze" v="7102"/>
                            <tag k="object_type" v="AX_Gebietsgrenze"/>
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                        </way>
                        <relation id='-10' changeset='-1'>
                            <member type='way' ref='-8' role='outer' />
                            <tag k='object_type' v='AX_KommunalesGebiet' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='admin_level' v='8' />
                            <tag k='boundary' v='administrative' />
                            <tag k='schluesselGesamt' v='0000000000' />
                        </relation>
                        <relation id='-11' changeset='-1'>
                            <member type='relation' ref='-10' role='' />
                            <tag k='object_type' v='AX_Gemeinde' />
                            <tag k='gemeindekennzeichen:gemeinde' v='00' />
                            <tag k='gemeindekennzeichen:kreis' v='00' />
                            <tag k='gemeindekennzeichen:land' v='00' />
                            <tag k='gemeindekennzeichen:gemeindeteil' v='00' />
                            <tag k='gemeindekennzeichen:regierungsbezirk' v='00' />
                            <tag k='bezeichnung' v='Test' />
                            <tag k='schluesselGesamt' v='00000000' />
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
}