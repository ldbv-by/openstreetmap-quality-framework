INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.F.b.005_009',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Polygon" },
                { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2010", "2011", "2012", "2013", "2070", "2090"] }
            ]
        },
        "checks": {
            "relations": {
                "conditions": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                        {
                            "relation_members": {
                                "loop_info": { "type": "any" },
                                "role": "over",
                                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" }
                            }
                        }
                    ]
                },
                "checks": {
                    "type": "spatial_compare",
                    "reference_feature_role": "under",
                    "operator": "covers",
                    "data_set_filter": { "memberFilter": { "role": "over", "objectTypes": ["AX_Fliessgewaesser"] } },
                    "self_check": true
                }
            }
        }
    }',
    'Ein Objekt ''AX_Fliessgewaesser'' das unter einem Objekt ''AX_BauwerkImGewaesserbereich'' mit ''bauwerksfunktion'' 2010, 2011, 2012, 2070 oder 2090 liegt, muss innerhalb der Umrissgemetrie des Bauwerks liegen.')
ON CONFLICT (id) DO NOTHING;