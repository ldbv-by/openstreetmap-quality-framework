INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.b.002',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Polygon" },
                { "type": "tag_between", "tag_key": "bauwerksfunktion", "from_value": "2030", "to_value": "2040" }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operator": "covered_by",
            "data_set_filter": {
                "criteria": {
                    "any": [
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_UnlandVegetationsloseFlaeche" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "1100" }
                            ]
                        },
                        {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_IndustrieUndGewerbeflaeche" },
                                { "type": "tag_equals", "tag_key": "funktion", "value": "2530" }
                            ]
                        }
                    ]
                }
            }
        }
    }',
    'Ein flächenförmiges Objekt ''AX_BauwerkImGewaesserbereich'' mit ''bauwerksfunktion'' 2030 bis 2040 liegt immer auf einer ''AX_UnlandVegetationsloseFlaeche'' mit ''funktion'' 1100 oder ''AX_IndustrieUndGewerbeflaeche'' mit ''funktion'' 2530.')
ON CONFLICT (id) DO NOTHING;