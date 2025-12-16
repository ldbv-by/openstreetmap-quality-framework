INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.51009.G.b.001',
    'geometry-check',
    'AX_SonstigesBauwerkOderSonstigeEinrichtung',
    '{
        "conditions": {
            "all": [
                { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1620" },
                { "not": { "type": "geom_type", "value": "Polygon" } }
            ]
        },
        "checks": {
            "any": [
                {
                    "all": [
                        { "type": "geom_type", "value": "LineString" },
                        { "type": "spatial_compare",
                          "operator": "equals_topo",
                          "data_set_filter": {
                              "criteria": {
                                  "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_WegPfadSteig"]
                              }
                          }
                        }
                    ]
                },
                {
                    "all": [
                        { "type": "geom_type", "value": "Point" },
                        { "type": "spatial_compare",
                          "operator": "covered_by",
                          "data_set_filter": {
                              "criteria": {
                                  "type": "tag_in", "tag_key": "object_type", "values": ["AX_Strassenachse", "AX_Fahrwegachse", "AX_WegPfadSteig"]
                              }
                          }
                        }
                    ]
                }
            ]
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1620 liegt immer auf einem Objekt ''AX_Strassenachse'', ''AX_Fahrwegachse'' oder ''AX_WegPfadSteig''.')
ON CONFLICT (id) DO NOTHING;