INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.004',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "not": {
                "type": "object_exists",
                "data_set_filter": { "featureFilter": { "tags": { "identifikator:UUID": "current:identifikator:UUID" } } }
            }
        }
    }',
    'Es existiert bereits ein Objekt mit dem Tag ''identifikator:UUID''.')
ON CONFLICT (id) DO NOTHING;
