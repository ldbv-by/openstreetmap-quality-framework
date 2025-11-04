INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53008.G.b.001_F.b.001',
    'geometry-check',
    'AX_EinrichtungenFuerDenSchiffsverkehr',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "Point" },
                { "type": "tag_equals", "tag_key": "art", "value": "1460" }
            ]
        },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by_boundary",
                    "data_set_filter": { "criteria": { "type": "tag_in", "tag_key": "object_type", "values": [ "AX_Fliessgewaesser", "AX_Hafenbecken", "AX_StehendesGewaesser", "AX_Meer" ] } }
                },
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": {
                        "criteria": {
                            "all": [
                                { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                                { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2133" }
                            ]
                        }
                    }
                }
            ]
        }
    }',
    'Ein Anleger liegt immer auf der Umrissgeometrie eines Objekts ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'' oder ''AX_Meer'' oder berühren ''AX_BauwerkImGewaesserbereich'' mit ''bauwerksfunktion'' 2133.')
ON CONFLICT (id) DO NOTHING;
