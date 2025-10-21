INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53009.G.d.010',
    'geometry-check',
    'AX_BauwerkImGewaesserbereich',
    '{
        "conditions": {
            "all": [
                { "type": "geom_type", "value": "LineString" },
                { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["2131", "2132", "2133"] }
            ]
        },
        "checks": {
            "type": "spatial_compare",
            "operators": [ "covered_by", "touches", "intersects" ],
            "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser|AX_Hafenbecken|AX_StehendesGewaesser|AX_Meer" } } }
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 2131, 2132 oder 2133 muss bei linienförmiger Modellierung innerhalb eines Objekts ''AX_Fliessgewaesser'', ''AX_Hafenbecken'', ''AX_StehendesGewaesser'' oder ''AX_Meer'' liegen, berühren oder kreuzen.')
ON CONFLICT (id) DO NOTHING;