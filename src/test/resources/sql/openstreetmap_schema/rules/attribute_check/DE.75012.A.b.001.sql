INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75012.A.b.001',
    'attribute-check',
    'AX_KommunalesTeilgebiet',
    '{
        "checks": {
            "relations": {
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gemeindeteil" },
                "checks": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:land", "value": "base:kennzeichen:land" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:regierungsbezirk", "value": "base:kennzeichen:regierungsbezirk" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:kreis", "value": "base:kennzeichen:kreis" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:gemeinde", "value": "base:kennzeichen:gemeinde" },
                        { "type": "tag_equals", "tag_key": "gemeindekennzeichen:gemeindeteil", "value": "base:kennzeichen:gemeindeteil" },
                        { "type": "tag_equals", "tag_key": "schluesselGesamt", "value": "base:schluesselGesamt" }
                    ]
                }
            }
        }
    }',
    'Das Tag ''kennzeichen'' muss einen vorhandenen ''AX_Gemeindeteil'' entsprechen.')
ON CONFLICT (id) DO NOTHING;
