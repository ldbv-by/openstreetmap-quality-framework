package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema;

import de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema.api.OsmSchemaService;
import de.bayern.bvv.geotopo.osm_quality_framework.quality_core.object_type.dto.*;
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

        Function<String, RelationDto> relation =
                o -> objectTypeDto.relations().stream()
                        .filter(r -> o.equals(r.objectType().name()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Relation not found: " + o));


        // --- artDerBebauung ---
        {
            var t = tag.apply("artDerBebauung");
            assertThat(t.type()).isEqualTo(Tag.Type.DICTIONARY);
            assertThat(t.multiplicity().min()).isEqualTo(1);
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

        // --- AG_thema (Relation)
        {
            var r = relation.apply("AG_thema");
            assertThat(r.multiplicity().min()).isEqualTo(0);
            assertThat(r.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(r.members()).containsExactlyInAnyOrder(
                    new MemberDto("*", "", new MultiplicityDto(1, 1)),
                    new MemberDto("*", "element", new MultiplicityDto(1, Integer.MAX_VALUE))
            );
        }

        // --- AA_istAbgeleitetAus (Relation)
        {
            var r = relation.apply("AA_istAbgeleitetAus");
            assertThat(r.multiplicity().min()).isEqualTo(0);
            assertThat(r.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(r.members()).containsExactlyInAnyOrder(
                    new MemberDto("*", "", new MultiplicityDto(1, 1)),
                    new MemberDto("*", "traegtBeiZu", new MultiplicityDto(1, Integer.MAX_VALUE))
            );
        }

        // --- AA_hatDirektUnten (Relation)
        {
            var r = relation.apply("AA_hatDirektUnten");
            assertThat(r.multiplicity().min()).isEqualTo(0);
            assertThat(r.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(r.members()).containsExactlyInAnyOrder(
                    new MemberDto("*", "over", new MultiplicityDto(1, Integer.MAX_VALUE)),
                    new MemberDto("*", "under", new MultiplicityDto(1, 1))
            );
        }

        // --- AA_zeigtAufExternes (Relation)
        {
            var r = relation.apply("AA_zeigtAufExternes");
            assertThat(r.multiplicity().min()).isEqualTo(0);
            assertThat(r.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(r.members()).containsExactlyInAnyOrder(
                    new MemberDto("*", "", new MultiplicityDto(1, 1))
            );
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

            var uuid = t.subTags().stream().filter(st -> "UUID".equals(st.key())).findFirst().orElseThrow();
            assertThat(uuid.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(uuid.multiplicity().min()).isEqualTo(1);
            assertThat(uuid.multiplicity().max()).isEqualTo(1);
            assertThat(uuid.dictionary()).isEmpty();
            assertThat(uuid.subTags()).isEmpty();

            var uuidZeit = t.subTags().stream().filter(st -> "UUIDundZeit".equals(st.key())).findFirst().orElseThrow();
            assertThat(uuidZeit.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(uuidZeit.multiplicity().min()).isEqualTo(1);
            assertThat(uuidZeit.multiplicity().max()).isEqualTo(1);
            assertThat(uuidZeit.dictionary()).isEmpty();
            assertThat(uuidZeit.subTags()).isEmpty();
        }

        // --- lebenszeitintervall (COMPLEX) ---
        {
            var t = tag.apply("lebenszeitintervall");
            assertThat(t.type()).isEqualTo(Tag.Type.COMPLEX);
            assertThat(t.multiplicity().min()).isEqualTo(1);
            assertThat(t.multiplicity().max()).isEqualTo(1);
            assertThat(t.dictionary()).isEmpty();
            assertThat(t.subTags()).hasSize(2);

            var beginnt = t.subTags().stream().filter(st -> "beginnt".equals(st.key())).findFirst().orElseThrow();
            assertThat(beginnt.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(beginnt.multiplicity().min()).isEqualTo(1);
            assertThat(beginnt.multiplicity().max()).isEqualTo(1);

            var endet = t.subTags().stream().filter(st -> "endet".equals(st.key())).findFirst().orElseThrow();
            assertThat(endet.type()).isEqualTo(Tag.Type.PRIMITIVE);
            assertThat(endet.multiplicity().min()).isEqualTo(0);
            assertThat(endet.multiplicity().max()).isEqualTo(1);
        }

        // --- AA_modellart (Relation)
        {
            var r = relation.apply("AA_modellart");
            assertThat(r.multiplicity().min()).isEqualTo(1);
            assertThat(r.multiplicity().max()).isEqualTo(Integer.MAX_VALUE);
            assertThat(r.members()).containsExactlyInAnyOrder(
                    new MemberDto("*", "", new MultiplicityDto(1, 1))
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