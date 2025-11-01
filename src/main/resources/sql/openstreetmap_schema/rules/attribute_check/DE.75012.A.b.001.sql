INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.A.b.001',
    'attribute-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": {
            "type": "object_exists",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gemeindeteil" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:land", "value": "current:kennzeichen:land" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:regierungsbezirk", "value": "current:kennzeichen:regierungsbezirk" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:kreis", "value": "current:kennzeichen:kreis" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:gemeinde", "value": "current:kennzeichen:gemeinde" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:gemeindeteil", "value": "current:kennzeichen:gemeindeteil" }
                    ]
                }
            }
        }
    }',
    'Das Tag ''kennzeichen'' muss einen vorhandenen ''AX_Gemeindeteil'' entsprechen.')
ON CONFLICT (id) DO NOTHING;
