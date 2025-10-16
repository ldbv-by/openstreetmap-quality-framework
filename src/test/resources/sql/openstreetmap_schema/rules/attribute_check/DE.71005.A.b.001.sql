INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71005.A.b.001',
    'attribute-check',
    'AX_SchutzgebietNachWasserrecht',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "funktion" },
        "checks": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "1510" }
    }',
    'Das Tag ''funktion'' darf nur bei der ''artDerFestlegung'' 1510 vorkommen.')
ON CONFLICT (id) DO NOTHING;
