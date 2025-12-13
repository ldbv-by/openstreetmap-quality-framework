INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.42002.F.b.005',
    'attribute-check',
    'AX_Strasse',
    '{
        "conditions": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "aggregator": "union",
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Platz" },
                        { "type": "tag_equals", "tag_key": "funktion", "value": "5330" }
                    ]
                }
            }
        },
        "checks": {
            "all": [
                { "type": "tag_equals", "tag_key": "widmung", "value": "1301" },
                { "type": "tag_regex_match", "tag_key": "bezeichnung", "pattern": "^A[A-Za-z0-9äÄöÖüÜ]*$" },
                { "not": { "type": "tag_exists", "tag_key": "internationaleBedeutung" } }
            ]
        }
    }',
    'Ein Strasse auf einer Raststätte muss Widmung 1301, keine internationaleBedeutung und die Bezeichnung mit A beginnen.')
ON CONFLICT (id) DO NOTHING;
