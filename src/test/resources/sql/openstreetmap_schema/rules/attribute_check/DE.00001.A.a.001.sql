INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.001',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "all": [
                { "type": "tag_regex_match", "tag_key": "lebenszeitintervall:beginnt", "pattern": "^20[0-9]{2}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z" },
                { "any": [
					{ "not": { "type": "tag_exists", "tag_key": "lebenszeitintervall:endet" } },
					{ "type": "tag_regex_match", "tag_key": "lebenszeitintervall:endet", "pattern": "^20[0-9]{2}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z" }
				]}
            ]
        }
    }',
    'Die Zeitangabe ist nicht GeoInfoDok-konform.')
ON CONFLICT (id) DO NOTHING;