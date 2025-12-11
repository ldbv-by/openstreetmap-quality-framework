INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.b.003_F.b.004',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2010", "2011", "2012", "2013", "2070", "2090"] }
            ]
        },
        "checks": {
            "relations": {
                "conditions": { "type": "tag_equals", "tag_key": "object_type", "value": "AA_hatDirektUnten" },
                "checks": {
                    "type": "spatial_compare",
                    "reference_feature_role": "under",
                    "operator": "equals_topo",

                    "data_set_filter": { "aggregator": "union", "memberFilter": { "role": "over", "objectTypes": ["AX_Gewaesserachse"] } },
                    "self_check": true
                }
            }
        }
    }',
    'Ein linienförmiges Objekt ''AX_BauwerkImGewaesserbereich'' mit ''bauwerksfunktion'' 2010 bis 2013, 2070 und 2090 überlagern ein Objekt ''AX_Gewaesserachse'' mit identischer Geometrie.')
ON CONFLICT (id) DO NOTHING;