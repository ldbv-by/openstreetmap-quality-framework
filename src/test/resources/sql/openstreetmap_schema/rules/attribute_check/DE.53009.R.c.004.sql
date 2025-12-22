INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.R.c.004',
    'attribute-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                {
                    "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2030", "2040", "2050", "2060", "2080", "2131", "2133"]
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": {
                        "criteria": {
                            "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenverkehr", "AX_Strassenachse", "AX_Fahrbahnachse", "AX_Fahrwegachse",
                                                                                   "AX_Bahnverkehr", "AX_Bahnstrecke", "AX_WegPfadSteig", "AX_Gleis"]
                        }
                    }
                }
            ]
        },


        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" }
            }
        }
    }',
    'Das Bauwerk wird nicht korrekt referenziert.')
ON CONFLICT (id) DO NOTHING;
