INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.A.c.001',
    'attribute-check',
    'AX_Gewaesserstationierungsachse',
    '{ "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserstationierungsachse" } }',
/*    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "2000" },
                {
                    "any": [
                        {
                            "relations": {
                                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                "checks": {
                                    "type": "spatial_compare",
                                    "relation_master_role": "over",
                                    "operator": "within",
                                    "aggregator": "union",
                                    "relation_compare_role": "under",
                                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser", "funktion": "8300" } } }
                                }
                            }
                        },
                        {
                            "all": [
                                "not": { "relations": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                                {
                                    "type": "spatial_compare",
                                    "operator": "within",
                                    "aggregator": "union",
                                    "data_set_filter": {
                                        "criteria": {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "AX_Fliessgewaesser" },
                                                { "type": "tag_equals", "tag_key": "funktion", "8300" },
                                                { "not": { "relations": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" } }
                                            ]
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
    }',*/
    'Eine Gewässerstationierungsachse mit AGA 2000, die (vollständig) in einem oder mehreren Fließgewässern mit FKT 8300 liegt, hat FLR ''FALSE''')
ON CONFLICT (id) DO NOTHING;

/*
Führt das Objekt AX_Gewaesserstationierungsachse mit 'artDerGewaesserachse'=2000 eine Relation 'hatDirektUnten',
so muss dieses gegen ein oder mehrere Objekte AX_Fliessgewaesser mit 'funktion'=8300 (Kanal) mit identischer Relation 'hatDirektUnten' geprüft werden.
Führt das Objekt AX_Gewaesserstationierungsachse mit 'artDerGewaesserachse'=2000 keinen Wert für hatDirektUnten,
so sind nur Fließgewässer ohne Wert für hatDirektUnten zu berücksichtigen.

Eine Gewässerstationierungsachse mit AGA 2000, die (vollständig) in einem oder mehreren Fließgewässern mit FKT 8300 liegt,
hat FLR 'FALSE'.
*/

/*

Wenn bei einem Objekt 42003 der Attributart 'BVB' die Werteart 1000 'Überörtlicher Durchgangsverkehr' belegt ist,
schließt geometrisch beidseitig immer ein weiteres Objekt 42003 mit BVB 1000 an.

Wenn dabei ein REO 42003 AX_Strassenachse mit Attributbelegung BVB 1000 an ein
REO 42005 AX_Fahrbahnachse geometrisch anschließt das zu einem ZUSO AX_Strasse mit einem REO AX_Strassenachse gehört
das die Attributbelegung BVB 1000 führt, wird keine Fehlermeldung erzeugt.

Ein Objekt 42003 mit BVB 1000 das an einem Objekt 57002 AX_SchifffahrtslinieFaehrverkehr ART 1710 Autofährverkehr
oder 75009 Gebietsgrenze mit AGZ 7102 Landesgrenze endet, schließt geometrisch nur einseitig an einem
weiteren Objekt 42003 mit BVB 1000 an.

Ist das zu untersuchende Objekt 42003 mit BVB 1000 Bestandteil eines ZUSO 42002 AX_Strasse mit FTR 2000,
bei dem an den zum ZUSO gehörenden Objekten 42005 AX_Fahrbahnachse jeweils
1. an mindestens einem Ende ein Objekt 42003 AX_Strassenachse mit BVB 1000 anschließt,
dessen Objektidentifikator unterschiedlich zu dem zu untersuchenden Objekt ist und das
Bestandteil eines ZUSO 42002 AX_Strasse ohne FTR 2000 ist oder
2. an beiden Enden weitere Objekte 42005 AX_Fahrbahnachse anschließen,
ist ebenfalls keine Fehlermeldung auszugeben.
*/