INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.001_003_F.d.001_6',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": {
            "all": [
                { "not": { "type": "tag_equals", "tag_key": "internationaleBedeutung", "value": "2001" } },
                { "type": "tag_equals", "tag_key": "widmung", "value": "1305" }
            ]
        },
        "checks": { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^(L|S)[A-Za-z0-9äÄöÖüÜ]*$" }

    }',
    'Das Tag ''bezeichnung'' muss bei ''widmung'' 1305 mit ''L'' oder ''S'' beginnen.')
ON CONFLICT (id) DO NOTHING;


