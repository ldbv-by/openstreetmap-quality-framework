INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.73005.A.b.001',
    'attribute-check',
    'AX_Gemeinde',
    '{
        "conditions": {
            "any": [
                { "type": "tag_exists", "tag_key": "istTeilVonVerwaltungsgemeinschaft:rolle" },
                { "type": "tag_exists", "tag_key": "istTeilVonVerwaltungsgemeinschaft:schluessel:kreis" },
                { "type": "tag_exists", "tag_key": "istTeilVonVerwaltungsgemeinschaft:schluessel:land" },
                { "type": "tag_exists", "tag_key": "istTeilVonVerwaltungsgemeinschaft:schluessel:regierungsbezirk" },
                { "type": "tag_exists", "tag_key": "istTeilVonVerwaltungsgemeinschaft:schluessel:verwaltungsgemeinschaft" }
            ]
        },
        "checks": { "not": { "type": "tag_exists", "tag_key": "gemeindekennzeichen:gemeindeteil" } }
    }',
    'Das Tag ''gemeindekennzeichen:gemeindeteil'' darf nicht belegt sein, da die Gemeinde ein Teil einer Verwaltungsgemeinschaft ist.')
ON CONFLICT (id) DO NOTHING;
