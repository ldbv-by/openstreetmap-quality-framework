INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75003.G.a.003',
    'geometry-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": {
            "not": {
                "type": "object_exists",
                "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_KommunalesGebiet",
                                                                  "gemeindekennzeichen:land": "current:kennzeichen:land",
                                                                  "gemeindekennzeichen:regierungsbezirk": "current:kennzeichen:regierungsbezirk",
                                                                  "gemeindekennzeichen:kreis": "current:kennzeichen:kreis",
                                                                  "gemeindekennzeichen:gemeinde": "current:kennzeichen:gemeinde",
                                                                  "gemeindekennzeichen:gemeindeteil": "current:kennzeichen:gemeindeteil" } } }
            }
        }
    }',
    'Es darf kein Objekt ''AX_KommunalesGebiet'' existieren, in der das ''kennzeichen'' mit ''gemeindekennzeichen'' identisch ist.')
ON CONFLICT (id) DO NOTHING;


