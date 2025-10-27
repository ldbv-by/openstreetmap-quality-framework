INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.31001.B.c.001',
    'attribute-check',
    'AX_Gebaeude',
    '{
        "conditions": { "type": "tag_exists", "tag_key": "weitereGebaeudefunktion" },
        "checks": {
            "not": {
                "any": [
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1000" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2030" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1010" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2071" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1020" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2072" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1030" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2081" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1040" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2092" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1050" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2094" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1060" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2465" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1070" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2462" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1080" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2612" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1090" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3013" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1100" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3014" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1110" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3032" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1120" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3034" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1130" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3037" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1140" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3043" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1150" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3046" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1160" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3047" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1170" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2056" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1180" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3071" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1200" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3290" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1210" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3065" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1220" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "3053" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1230" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2052" }
                        ]
                    },
                    {
                        "all": [
                            { "type": "tag_equals", "tag_key": "weitereGebaeudefunktion", "value": "1240" },
                            { "type": "tag_equals", "tag_key": "gebaeudefunktion", "value": "2050" }
                        ]
                    }
                ]
            }
        }
    }',
    'Der Wert von ''weitereGebaeudefunktion'' darf nicht identisch mit dem Wert von ''gebaeudefunktion'' sein.')
ON CONFLICT (id) DO NOTHING;
