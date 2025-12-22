INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.R.b.001',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "checks": {
            "any": [
                { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1880", "1890", "1900"] },
                {
                    "relations": {
                        "loop_info": { "type": "all" },
                        "conditions": {
                            "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten"
                        },
                        "checks": {
                            "relation_members": {
                                "loop_info": { "type": "all" },
                                "checks": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_WegPfadSteig", "AX_Bahnstrecke", "AX_Gleis",
                                                                                                   "AX_Gewaesserachse", "AX_Strassenverkehr", "AX_Bahnverkehr", "AX_Fliessgewaesser",
                                                                                                   "AX_Fahrbahnachse", "AX_BauwerkImVerkehrsbereich"] }
                            }
                        }
                    }
                }
            ]
        }
    }',
    'Das Bauwerk wird nicht korrekt referenziert.')
ON CONFLICT (id) DO NOTHING;