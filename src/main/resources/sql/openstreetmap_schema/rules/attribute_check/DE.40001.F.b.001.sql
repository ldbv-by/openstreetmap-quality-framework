INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.40001.F.b.001',
    'attribute-check',
    'AX_TatsaechlicheNutzung',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "istWeitereNutzung" },
        "checks": { "type": "tag_in", "tag_key": "object_type", "values": ["AX_Wohnbauflaeche",
                                                                           "AX_IndustrieUndGewerbeflaeche",
                                                                           "AX_Bergbaubetrieb",
                                                                           "AX_TagebauGrubeSteinbruch",
                                                                           "AX_FlaecheGemischterNutzung",
                                                                           "AX_FlaecheBesondererFunktionalerPraegung",
                                                                           "AX_SportFreizeitUndErholungsflaeche",
                                                                           "AX_Friedhof",
                                                                           "AX_Strassenverkehr",
                                                                           "AX_Bahnverkehr",
                                                                           "AX_Flugverkehr",
                                                                           "AX_Schiffsverkehr"] }
    }',
    'Das Tag ''istWeitereNutzung'' darf nur in Verbindung mit Objekten der Objektart 41001, 41002, 41004, 41005, 41006, 41007, 41008, 41009, 42001, 42010, 42015 und 42016 vorkommen.')
ON CONFLICT (id) DO NOTHING;
