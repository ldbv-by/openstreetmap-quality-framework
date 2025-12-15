INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44002.A.c.001_44004.F.c.002_003',
    'attribute-check',
    'AX_Wasserlauf',
    '{
        "checks": {
            "any": [
                {
                    "relation_members": {
                        "conditions": {  "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "TRUE" }
                    }
                },
                {
                    "relation_members": {
                        "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                        "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
                    }
                }
            ]
        }
    }',
    'Alle Objekte ''AX_Gewaesserachse'' in einer Relation ''AX_Wasserlauf'' müssen die gleiche ''fliessrichtung'' haben.')
ON CONFLICT (id) DO NOTHING;
