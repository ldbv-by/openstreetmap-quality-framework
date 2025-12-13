INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.001_002',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "internationaleBedeutung" },
        "checks": {
            "all": [
                { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^E[A-Za-z0-9äÄöÖüÜ]*$", "minCount": "1" },
                { "type": "tag_in", "tag_key": "widmung", "values": [ "1301", "1303" ] }
            ]
        }
    }',
    'Das Tag ''internationaleBedeutung'' kann nur vorkommen, wenn das Tag ''widmung'' 1301 oder 1303 ist und eine Bezeichnung mit E beginnt.')
ON CONFLICT (id) DO NOTHING;
