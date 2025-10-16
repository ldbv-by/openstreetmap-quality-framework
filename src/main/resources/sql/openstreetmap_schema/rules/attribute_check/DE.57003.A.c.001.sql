INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.57003.A.c.001',
    'attribute-check',
    'AX_Gewaesserstationierungsachse',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "2000" },
                { "type": "spatial_compare",
                  "operator": "within",
                  "data_set_filter": {  "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser", "funktion": "8300" } } }
                }
            ]
        },
        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
    }',
    'Das Tag ''fliessrichtung'' hat den Wert ''FALSE'', wenn eine Gewässerstationierungsache mit AGA 2000 vollständig in einem oder mehreren Fließgewässern liegt.')
ON CONFLICT (id) DO NOTHING;

/*
Führt das Objekt AX_Gewaesserstationierungsachse mit 'artDerGewaesserachse'=2000 eine Relation 'hatDirektUnten',
so muss dieses gegen ein oder mehrere Objekte AX_Fliessgewaesser mit 'funktion'=8300 (Kanal) mit identischer Relation 'hatDirektUnten' geprüft werden.
Führt das Objekt AX_Gewaesserstationierungsachse mit 'artDerGewaesserachse'=2000 keinen Wert für hatDirektUnten,
so sind nur Fließgewässer ohne Wert für hatDirektUnten zu berücksichtigen.
*/