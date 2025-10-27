INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.31002.B.b.001',
    'attribute-check',
    'AX_Bauteil',
    '{
        "conditions" : { "type": "tag_exists", "tag_key": "durchfahrtshoehe" },
        "checks": { "type": "tag_in", "tag_key": "bauart", "values": ["2610", "2620"] }
    }',
    'Das Tag ''durchfahrtshoehe'' darf nur bei der ''bauart'' 2610 und 2620 geführt werden.')
ON CONFLICT (id) DO NOTHING;
