INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.004',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": { "type": "unique_check", "tag_key": "identifikator:UUID", "scope": "global" }
    }',
    'Das Tag ''identifikator:UUID'' muss global eindeutig sein.')
ON CONFLICT (id) DO NOTHING;
