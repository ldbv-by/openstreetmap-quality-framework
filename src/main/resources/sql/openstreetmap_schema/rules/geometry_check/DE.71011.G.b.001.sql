INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.71011.G.b.001',
    'geometry-check',
    'AX_SonstigesRecht',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "artDerFestlegung", "value": "5400" },
        "checks": {
            "any": [
                {
                    "type": "spatial_compare",
                    "operator": "covered_by",
                    "data_set_filter": { "featureFilter": { "tags": { "object_type": "AX_Landwirtschaft|AX_Wald|AX_Gehoelz|AX_Heide|AX_Moor|AX_Sumpf|AX_UnlandVegetationsloseFlaeche|AX_FlaecheZurZeitUnbestimmbar" } } }
                }
            ]
        }
    }',
    'Ein Objekt ''AX_SonstigesRecht'' mit ''artDerFestlegung'' 5400 muss auf einem Objekt der Objektartengruppe ''Vegetation'' liegen.')
ON CONFLICT (id) DO NOTHING;

