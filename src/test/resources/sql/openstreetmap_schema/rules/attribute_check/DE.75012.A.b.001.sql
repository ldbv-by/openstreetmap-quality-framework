INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.A.b.001',
    'attribute-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": {
            "type": "object_exists",
            "data_set_filter": { "includedChangesetIds": [1],
                                 "featureFilter": { "tags": { "gemeindekennzeichen:land": "current:kennzeichen:land",
                                                              "gemeindekennzeichen:regierungsbezirk": "current:kennzeichen:regierungsbezirk",
                                                              "gemeindekennzeichen:kreis": "current:kennzeichen:kreis",
                                                              "gemeindekennzeichen:gemeinde": "current:kennzeichen:gemeinde",
                                                              "gemeindekennzeichen:gemeindeteil": "current:kennzeichen:gemeindeteil" } } }
        }
    }',
    'Das Tag ''kennzeichen'' muss einen vorhandenen ''AX_Gemeindeteil'' entsprechen.')
ON CONFLICT (id) DO NOTHING;
