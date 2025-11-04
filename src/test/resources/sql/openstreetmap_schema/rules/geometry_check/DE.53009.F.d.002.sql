INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.d.010_F.d.001',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Polygon" },
                { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "2130", "to_value": "2136" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "all": [
                        { "type": "tag_equals", "tag_key": "object_type", "value": "AX_UnlandVegetationsloseFlaeche" },
                        { "type": "tag_in", "tag_key": "funktion", "values": ["1000", "1100", "1110", "1120"] }
                    ]
                }
            }
        }
    }',
    'Ein Objekt ''AX_BauwerkImGewaesserbereich''  der ''bauwerksfunktion'' 2130 bis 2136 liegt immer auf einem Objekt ''AX_UnlandVegetationsloseFlaeche'' mit ''funktion'' 1000, 1100, 1110 oder 1120.')
ON CONFLICT (id) DO NOTHING;
