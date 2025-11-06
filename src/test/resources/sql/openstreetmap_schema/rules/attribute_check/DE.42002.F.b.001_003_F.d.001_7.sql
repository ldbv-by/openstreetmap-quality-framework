INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.001_003_F.d.001_7',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": {
            "type": "tag_in", "tag_key": "widmung", "values": ["1307", "9997", "9999"]
        },
        "checks": { "not": { "type": "tag_exists", "tag_key": "bezeichnung" } }

    }',
    'Das Tag ''bezeichnung'' darf bei ''widmung'' 1307, 9997 oder 9999 nicht befüllt sein.')
ON CONFLICT (id) DO NOTHING;


