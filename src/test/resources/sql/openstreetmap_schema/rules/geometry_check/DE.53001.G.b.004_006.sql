INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.53001.G.b.004_006',
    'geometry-check',
    'AX_BauwerkImVerkehrsbereich',
    '{
        "conditions": { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "1890" },
        "checks": {
            "all": [
                { "type": "spatial_compare",
                  "operator": "covered_by",
                  "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Schleuse" } } }
                },
                {
                    "any": [
                        {
                            "all": [
                                { "type": "geom_type", "value": "LineString" },
                                { "type": "spatial_compare",
                                  "operator": "equals_topo",
                                  "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Gewaesserachse" } } }
                                }
                            ]
                        },
                        {
                            "all": [
                                { "type": "geom_type", "value": "Point" },
                                { "type": "spatial_compare",
                                  "operator": "covered_by",
                                  "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Gewaesserachse" } } }
                                }
                            ]
                        },
                        {
                            "all": [
                                { "type": "geom_type", "value": "Polygon" },
                                { "type": "spatial_compare",
                                  "operator": "covered_by",
                                  "data_set_filter": { "includedChangesetIds": [1], "featureFilter": { "tags": { "object_type": "AX_Fliessgewaesser" } } }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    }',
    'Ein Objekt mit der ''bauwerksfunktion'' 1890 befindet sich immer innerhalb einer ''AX_Schleue'' und bei linienförmiger Modellierung muss geometrieidentisch mit einem Objekt ''AX_Gewaesserachse'' sein. Bei punktförmiger Modellierung muss dieser auf einer ''AX_Gewaesserachse'' liegen. Bei flächenförmiger Modellierung wird ein ''AX_Fliessgewaesser'' überlagert.')
ON CONFLICT (id) DO NOTHING;
