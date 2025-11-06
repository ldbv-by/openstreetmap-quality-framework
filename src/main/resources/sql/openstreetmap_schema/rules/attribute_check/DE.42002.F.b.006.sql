INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.006',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                { "type": "tag_in", "tag_key": "widmung", "values": ["1301", "1303"] }
            ]
        },
        "checks": { "type": "tag_exists", "tag_key": "bezeichnung" }
    }',
    'Das Tag ''bezeichnung'' muss bei ''widmung'' 1301 oder 1303 und ohne ''internationaleBedeutung'' belegt sein.')
ON CONFLICT (id) DO NOTHING;
