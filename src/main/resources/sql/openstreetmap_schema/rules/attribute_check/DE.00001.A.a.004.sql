INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.004',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "not": {
                "type": "object_exists",
                "data_set_filter": {
                    "criteria": {
                        "type": "tag_equals", "tag_key": "identifikator:UUID", "value": "current:identifikator:UUID"
                    }
                }
            }
        }
    }',
    'Der Objektidentifikator ist bereits vorhanden.')
ON CONFLICT (id) DO NOTHING;