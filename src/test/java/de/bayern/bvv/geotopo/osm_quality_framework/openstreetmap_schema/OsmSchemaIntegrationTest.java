package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.spi.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.ObjectTypeDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.TagDto;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.model.Tag;
import de.bayern.bvv.geotopo.osm_quality_framework.test_core.DatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class OsmSchemaIntegrationTest extends DatabaseIntegrationTest {
    @Autowired
    private OsmSchemaService osmSchemaService;

    @Test
    void testWohnbauflaeche() {
        // Arrange + Act
        ObjectTypeDto objectTypeDto = this.osmSchemaService.getObjectTypeInfo("AX_Wohnbauflaeche");

        // Assert
        assertThat(objectTypeDto).isNotNull();
        assertThat(objectTypeDto.name()).isEqualTo("AX_Wohnbauflaeche");
        assertThat(objectTypeDto.tags()).isNotNull().isNotEmpty();

        Function<String, TagDto> tag =
                k -> objectTypeDto.tags().stream()
                        .filter(t -> k.equals(t.key()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Tag not found: " + k));

        // --- artDerBebauung ---
        {
            var t = tag.apply("artDerBebauung");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.ofEntries(
                            Map.entry("1000", "Offen"),
                            Map.entry("2000", "Geschlossen")
                    )
            );
            assertThat(t.subTags()).isEmpty();
        }

        // --- name ---
        {
            var t = tag.apply("name");
            assertThat(t.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).isEmpty();
        }

        // --- zustand ---
        {
            var t = tag.apply("zustand");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.of("2100", "Außer Betrieb, stillgelegt, verlassen")
            );
            assertThat(t.subTags()).isEmpty();
        }

        // --- zweitname ---
        {
            var t = tag.apply("zweitname");
            assertThat(t.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).isEmpty();
        }

        // --- funktion ---
        {
            var t = tag.apply("funktion");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.of("1200", "Parken")
            );
            assertThat(t.subTags()).isEmpty();
        }

        // --- datumDerLetztenUeberpruefung ---
        {
            var t = tag.apply("datumDerLetztenUeberpruefung");
            assertThat(t.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).isEmpty();
        }

        // --- istWeitereNutzung ---
        {
            var t = tag.apply("istWeitereNutzung");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.of("1000", "Überlagernd")
            );
            assertThat(t.subTags()).isEmpty();
        }

        // --- ergebnisDerUeberpruefung ---
        {
            var t = tag.apply("ergebnisDerUeberpruefung");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.ofEntries(
                            Map.entry("1000", "Fehlerkorrektur"),
                            Map.entry("2000", "Bestätigung des Ist-Zustandes"),
                            Map.entry("3000", "Erfassung eines neuen Objektes"),
                            Map.entry("4000", "Geometrieveränderung eines bestehenden Objektes")
                    )
            );
            assertThat(t.subTags()).isEmpty();
        }

        // --- zeigtAufExternes (COMPLEX, max = Integer.MAX_VALUE) ---
        {
            var t = tag.apply("zeigtAufExternes");
            assertThat(t.type()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).hasSize(2);

            var art = t.subTags().stream().filter(st -> "art".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(art.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(art.getMultiplicity().min()).isEqualTo(1);
            assertThat(art.getMultiplicity().max()).isEqualTo(1);

            var fach = t.subTags().stream().filter(st -> "fachdatenobjekt".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(fach.getType()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(fach.getMultiplicity().min()).isEqualTo(1);
            assertThat(fach.getMultiplicity().max()).isEqualTo(1);
            assertThat(fach.getSubTags()).hasSize(2);

            var name = fach.getSubTags().stream().filter(st -> "name".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(name.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(name.getMultiplicity().min()).isEqualTo(1);
            assertThat(name.getMultiplicity().max()).isEqualTo(1);

            var uri = fach.getSubTags().stream().filter(st -> "uri".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(uri.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(uri.getMultiplicity().min()).isEqualTo(1);
            assertThat(uri.getMultiplicity().max()).isEqualTo(1);
        }

        // --- quellobjektID ---
        {
            var t = tag.apply("quellobjektID");
            assertThat(t.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).isEmpty();
        }

        // --- identifikator (COMPLEX) ---
        {
            var t = tag.apply("identifikator");
            assertThat(t.type()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(t.multiplicity().min()).isEqualTo(1);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).hasSize(2);

            var uuid = t.subTags().stream().filter(st -> "UUID".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(uuid.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(uuid.getMultiplicity().min()).isEqualTo(1);
            assertThat(uuid.getMultiplicity().max()).isEqualTo(1);
            assertThat(uuid.getDictionary()).isEmpty();
            assertThat(uuid.getSubTags()).isEmpty();

            var uuidZeit = t.subTags().stream().filter(st -> "UUIDundZeit".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(uuidZeit.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(uuidZeit.getMultiplicity().min()).isEqualTo(1);
            assertThat(uuidZeit.getMultiplicity().max()).isEqualTo(1);
            assertThat(uuidZeit.getDictionary()).isEmpty();
            assertThat(uuidZeit.getSubTags()).isEmpty();
        }

        // --- lebenszeitintervall (COMPLEX) ---
        {
            var t = tag.apply("lebenszeitintervall");
            assertThat(t.type()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(t.multiplicity().min()).isEqualTo(1);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).hasSize(2);

            var beginnt = t.subTags().stream().filter(st -> "beginnt".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(beginnt.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(beginnt.getMultiplicity().min()).isEqualTo(1);
            assertThat(beginnt.getMultiplicity().max()).isEqualTo(1);

            var endet = t.subTags().stream().filter(st -> "endet".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(endet.getType()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(endet.getMultiplicity().min()).isEqualTo(0);
            assertThat(endet.getMultiplicity().max()).isEqualTo(1);
        }

        // --- modellart (COMPLEX, max = Integer.MAX_VALUE) ---
        {
            var t = tag.apply("modellart");
            assertThat(t.type()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(t.multiplicity().min()).isEqualTo(1);
            assertThat(t.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).hasSize(2);

            var adv = t.subTags().stream().filter(st -> "advStandardModell".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(adv.getType()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(adv.getMultiplicity().min()).isEqualTo(1);
            assertThat(adv.getMultiplicity().max()).isEqualTo(1);
            assertThat(adv.getDictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.ofEntries(
                            Map.entry("Basis-DLM", "BasisLandschaftsModell"),
                            Map.entry("BRM", "Bodenrichtwertemodell"),
                            Map.entry("DFGM", "Festpunktmodell"),
                            Map.entry("DHM", "DigitalesHoehenmodell"),
                            Map.entry("DKKM1000", "KatasterkartenModell1000"),
                            Map.entry("DKKM2000", "KatasterkartenModell2000"),
                            Map.entry("DKKM500", "KatasterkartenModell500"),
                            Map.entry("DKKM5000", "KatasterkartenModell5000"),
                            Map.entry("DLKM", "LiegenschaftskatasterModell"),
                            Map.entry("DLM1000", "LandschaftsModell1000"),
                            Map.entry("DLM250", "LandschaftsModell250"),
                            Map.entry("DLM50", "LandschaftsModell50"),
                            Map.entry("DTK10", "TopographischeKarte10"),
                            Map.entry("DTK100", "TopographischeKarte100"),
                            Map.entry("DTK1000", "TopographischeKarte1000"),
                            Map.entry("DTK25", "TopographischeKarte25"),
                            Map.entry("DTK250", "TopographischeKarte250"),
                            Map.entry("DTK50", "TopographischeKarte50"),
                            Map.entry("GeoBasis-DE", "LandbedeckungLandnutzung"),
                            Map.entry("GVM", "GeometrischesVerbesserungsModell"),
                            Map.entry("LoD1", "LevelOfDetail1"),
                            Map.entry("LoD2", "LevelOfDetail2"),
                            Map.entry("LoD3", "LevelOfDetail3")
                    )
            );

            var sonst = t.subTags().stream().filter(st -> "sonstigesModell".equals(st.getKey())).findFirst().orElseThrow();
            assertThat(sonst.getType()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(sonst.getMultiplicity().min()).isEqualTo(1);
            assertThat(sonst.getMultiplicity().max()).isEqualTo(1);
            assertThat(sonst.getDictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.ofEntries(
                            Map.entry("DTK100A", "DigitaleTopographischeKarte100AKG"),
                            Map.entry("DTK10A", "DigitaleTopographischeKarte10AKG"),
                            Map.entry("DTK25A", "DigitaleTopographischeKarte25AKG"),
                            Map.entry("DTK50A", "DigitaleTopographischeKarte50AKG"),
                            Map.entry("TFIS25", "TopographischesFreizeitInformationsSystem25"),
                            Map.entry("TFIS50", "TopographischesFreizeitInformationsSystem50")
                    )
            );
        }

        // --- anlass ---
        {
            var t = tag.apply("anlass");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(0);
            assertThat(t.multiplicity().max()).isEqualTo(2);
            assertThat(t.dictionary()).containsExactlyInAnyOrderEntriesOf(
                    Map.ofEntries(
                            Map.entry("000000", "Ersteinrichtung"),
                            Map.entry("1000", "Grundaktualisierung"),
                            Map.entry("2000", "Spitzenaktualisierung"),
                            Map.entry("200000", "Veränderung von Gebäudedaten"),
                            Map.entry("200100", "Eintragen eines Gebäudes"),
                            Map.entry("200200", "Veränderung der Gebäudeeigenschaften"),
                            Map.entry("200300", "Löschen eines Gebäudes"),
                            Map.entry("300000", "Sonstige Daten fortführen"),
                            Map.entry("300200", "Veränderung von Bauwerken, Einrichtungen und sonstigen Angaben"),
                            Map.entry("300300", "Veränderung der tatsächlichen Nutzung"),
                            Map.entry("300500", "Veränderung aufgrund der Homogenisierung"),
                            Map.entry("300501", "Veränderung aufgrund der Kartenanpassung"),
                            Map.entry("300900", "Veränderung der Geometrie durch Implizitbehandlung"),
                            Map.entry("5000", "Interaktive Kartographische Generalisierung"),
                            Map.entry("6000", "Veränderung an der Landesgrenze und des Gebietes")
                    )
            );
            assertThat(t.subTags()).isEmpty();
        }
    }
}