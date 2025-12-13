INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44003.A.c.001_F.c.001',
    'attribute-check',
    'AX_Kanal',
    '{
        "checks": {
            "relation_members": {
                "conditions": {
                    "any": [
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "8300" }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserstationierungsachse" },
                                { "not": { "type": "tag_equals", "tag_key": "artDerGewaesserstationierungsachse", "value": "3001" } }
                            ]
                        }
                    ]
                },
                "checks": { "type": "tag_equals", "tag_key": "fliessrichtung", "value": "FALSE" }
            }
        }
    }',
    'Die zu ''AX_Kanal'' gehörenden ''AX_Gewaesserachse'' und ''AX_Gewaesserstationierungsachse'' dürfen keine Fliessrichtung haben.')
ON CONFLICT (id) DO NOTHING;