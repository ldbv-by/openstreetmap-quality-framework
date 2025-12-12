INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.001_003_F.d.001_2',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "internationaleBedeutung", "value": "2001" },
                { "type": "tag_equals", "tag_key": "widmung", "value": "1301" }
            ]
        },
        "checks": { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^(E|A)[A-Za-z0-9äÄöÖüÜ]*$", "min_count": "2" }

    }',
    'Das Tag ''bezeichnung'' muss mit ''E'' oder ''A'' beginnen und mindestens zwei Bezeichnungen vorkommen, wenn die ''internationaleBedeutung'' 2001 gesetzt ist.')
ON CONFLICT (id) DO NOTHING;


