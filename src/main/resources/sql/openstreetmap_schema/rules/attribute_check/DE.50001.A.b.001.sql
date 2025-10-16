INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.50001.A.b.001',
    'attribute-check',
    'AX_BauwerkeEinrichtungenUndSonstigeAngaben',
    '{
        "conditions": {
            "not": {
                "any": [
                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkOderAnlageFuerSportFreizeitUndErholung" },
                               { "type": "tag_in", "tag_key": "bauwerksfunktion", "values": ["1460","1480","1650"] }] },

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkOderAnlageFuerSportFreizeitUndErholung" },
                               { "type": "tag_between", "tag_key": "sportart", "from_value": "1010", "to_value": "1120" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_SonstigesBauwerkOderSonstigeEinrichtung" },
                               { "type": "tag_in", "tag_key": "funktion", "values": ["1000", "2000"] }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Hafen" },
                               { "type": "tag_between", "tag_key": "hafenkategorie", "from_value": "1010", "to_value": "1070" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Hafen" },
                               { "type": "tag_between", "tag_key": "nutzung", "from_value": "1000", "to_value": "3000" }]},

                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Schleuse" },

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_WegPfadSteig" },
                               { "type": "tag_in", "tag_key": "art",  "values": ["1103", "1106", "1107", "1110"] }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Bahnverkehrsanlage" },
                               { "type": "tag_between", "tag_key": "bahnhofskategorie", "from_value": "1010", "to_value": "1030" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Bahnverkehrsanlage" },
                               { "type": "tag_in", "tag_key": "zustand", "values": ["2100", "4000"] }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Flugverkehrsanlage" },
                               { "type": "tag_between", "tag_key": "art", "from_value": "1310", "to_value": "1330" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Flugverkehrsanlage" },
                               { "type": "tag_equals", "tag_key": "art", "value": "5560" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Flugverkehrsanlage" },
                               { "type": "tag_in", "tag_key": "zustand", "values": ["2100", "4000"] }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_EinrichtungenFuerDenSchiffsverkehr" },
                               { "type": "tag_in", "tag_key": "art", "values": ["1460","1470"] }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_BauwerkImGewaesserbereich" },
                               { "type": "tag_equals", "tag_key": "bauwerksfunktion", "value": "2020" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Vegetationsmerkmal" },
                               { "type": "tag_equals", "tag_key": "bewuchs", "value": "1300" }]},

                    { "all": [ { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Vegetationsmerkmal" },
                               { "type": "tag_equals", "tag_key": "funktion", "value": "1000" }]},

                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_Polder" }
                ]
            }
        },

        "checks": { "not": { "type": "tag_exists", "tag_key": "ergebnisDerUeberpruefung" } }
    }',
    'Das Tag ''ergebnisDerUeberpruefung'' kann nur in bestimmten Konstellationen verwendet werden.')
ON CONFLICT (id) DO NOTHING;
