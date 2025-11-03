INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53002.G.b.001',
    'geometry-check',
    'AX_Strassenverkehrsanlage',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "art", "value": "2000" },
                { "type": "geom_type", "value": "LineString" }
            ]
        },
        "checks": {
            "any": [
                {
                    "all": [
                        {
                            "type": "spatial_compare",
                            "operator": "equals_topo",
                            "data_set_filter": { "criteria": { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Strassenachse", "AX_Fahrwegachse" ] } }
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "covered_by_boundary",
                            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" } }
                        }
                    ]
                },
                {
                    "all": [
                        {
                            "type": "spatial_compare",
                            "operator": "equals_topo",
                            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_WegPfadSteig" } }
                        },
                        {
                            "type": "spatial_compare",
                            "operator": "covered_by",
                            "data_set_filter": { "criteria": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" } }
                        }
                    ]
                }
            ]
        }
    }',
    'Das Objekt mit ''art'' 2000 überlagert bei linienförmiger Modellierung immer ein Objekt ''AX_Strassenachse'', ''AX_Fahrwegachse'' oder ''AX_WegPfadSteig'' mit identischer Geometrie auf der Umrissgeometrie ''AX_Fliessgewaesser''. ''AX_Fliessgewaesser'' wird durch die Maschenbildner getrennt (Ausnahme bei ''AX_WegPfadSteig'').')
ON CONFLICT (id) DO NOTHING;