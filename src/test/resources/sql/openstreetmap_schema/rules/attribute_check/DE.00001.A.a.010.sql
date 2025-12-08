INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.00001.A.a.010',
    'attribute-check',
    'AA_Objekt',
    '{
        "checks": {
            "type": "tag_starts_with",
            "tag_key": "identifikator:UUIDundZeit",
            "value": "current:identifikator:UUID"
        }
    }',
    'Das Tag ''identifikator:UUIDundZeit'' muss mit ''identifikator:UUID'' beginnen.')
ON CONFLICT (id) DO NOTHING;
