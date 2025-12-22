INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.R.b.001',
    'attribute-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "checks": {
            "any": [
                {
                    "all": [
                        { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2020", "2050", "2080", "2131", "2133"] },
                        {
                            "relations": {
                                "loop_info": { "type": "none" },
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
                            }
                        }
                    ]
                },
                {
                    "relations": {
                        "loop_info": { "type": "all" },
                        "conditions": {
                            "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten"
                        },
                        "checks": {
                            "relation_members": {
                                "loop_info": { "type": "all" },
                                "checks": {
                                    "any": [
                                        {
                                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Gewaesserachse", "AX_Fliessgewaesser", "AX_Gewaesserstationierungsachse"]
                                        },
                                        {
                                            "all": [
                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                                { "not": { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2020", "2050", "2080", "2131", "2133"] } }
                                            ]
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Das Bauwerk wird nicht korrekt referenziert.')
ON CONFLICT (id) DO NOTHING;