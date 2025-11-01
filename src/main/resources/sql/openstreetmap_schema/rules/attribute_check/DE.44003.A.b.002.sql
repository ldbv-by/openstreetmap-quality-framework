INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.44003.A.b.002',
    'attribute-check',
    'AX_Kanal',
    '{
        "checks": {
            "relation_members": {
                "conditions": {
                    "any": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Gewaesserachse" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "8300" }
                            ]
                        }
                    ]
                },
                "checks": { "not": { "type": "tag_exists", "tag_key": "hydrologischesMerkmal" } }
            }
        }
    }',
    'Die Relation ''AX_Kanal'' darf auf den dazugehörenden ''AX_Fliessgewaesser'' und ''AX_Gewaesserachse'' mit ''funktion'' 8300 das Tag ''hydrologischesMerkmal'' nicht führen.')
ON CONFLICT (id) DO NOTHING;