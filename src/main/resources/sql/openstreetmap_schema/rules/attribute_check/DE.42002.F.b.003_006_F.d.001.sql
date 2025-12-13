INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.003_006_F.d.001',
    'attribute-check',
    'AX_Strasse',
    '{
        "checks": {
            "any": [
                {
                    "all": [
                        { "type": "tag_exists", "tag_key": "internationaleBedeutung" },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1301" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^A[A-Za-z0-9äÄöÖüÜ]*$", "min_count": "1", "max_count": "1" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^E[A-Za-z0-9äÄöÖüÜ]*$", "min_count": "1" }
                    ]
                },
                {
                    "all": [
                        { "type": "tag_exists", "tag_key": "internationaleBedeutung" },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1303" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^B[A-Za-z0-9äÄöÖüÜ]*$", "min_count": "1", "max_count": "1" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^E[A-Za-z0-9äÄöÖüÜ]*$", "min_count": "1" }
                    ]
                },
                {
                    "all": [
                        { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1301" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^A[A-Za-z0-9äÄöÖüÜ]*$" }
                    ]
                },
                {
                    "all": [
                        { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1303" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^B[A-Za-z0-9äÄöÖüÜ]*$" }
                    ]
                },
                {
                    "all": [
                        { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1305" },
                        { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^(L|S)[A-Za-z0-9äÄöÖüÜ]*$" }
                    ]
                },
                {
                    "all": [
                        { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                        { "type": "tag_in", "tag_key": "widmung", "values": ["1307", "9997", "9999"] },
                        { "not": { "type": "tag_exists", "tag_key": "bezeichnung" } }
                    ]
                },
                {
                    "all": [
                        { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } },
                        { "type": "tag_equals", "tag_key": "widmung", "value": "1306" },
                        { "type": "tag_exists", "tag_key": "bezeichnung" }
                    ]
                }
            ]
        }
    }',
    'Keine gültige Bezeichnung-, internationaleBedeutung- und Widmungskombination.')
ON CONFLICT (id) DO NOTHING;
