INSERT INTO openstreetmap_schema.rules (id, type, object_type, expression, error_text) VALUES (
    'DE.75009.G.b.001_009_F.a.001_004',
    'geometry-check',
    'AX_Gebietsgrenze',
    '{
        "conditions": {
            "not": {
                "any": [
                    {   "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": ["7107"] },
                    {
                        "all": [
                            {   "type": "tag_in", "tag_key": "artDerGebietsgrenze", "values": ["7101", "7102"] },
                            {
                                "type": "spatial_compare",
                                "operator": "covered_by",
                                "data_set_filter": {
                                    "criteria": {
                                        "any": [
                                            {
                                                "all": [
                                                    { "type": "tag_equals", "tag_key": "object_type", "value": "AX_StehendesGewaesser" },
                                                    { "type": "tag_equals", "tag_key": "name", "value": "Bodensee" }
                                                ]
                                            },
                                            {   "type": "tag_equals", "tag_key": "object_type", "value": "AX_Meer" },
                                            {
                                                "all": [
                                                    {   "type": "tag_equals", "tag_key": "object_type", "value": "AX_Fliessgewaesser" },
                                                    {
                                                        "type": "relation_exists",
                                                        "criteria": {
                                                            "all": [
                                                                { "type": "tag_equals", "tag_key": "object_type", "value": "AA_zeigtAufExternes" },
                                                                { "type": "tag_regex_match", "tag_key": "art", "pattern": "^urn:[A-Za-z]+:fdv:1900$" },
                                                                { "type": "tag_equals", "tag_key": "fachdatenobjekt:name", "value": "FKT8230" }
                                                            ]
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        },

        "checks": {
            "relations": {
                "loop_info": { "type": "any" },
                "checks": { "type": "tag_equals", "tag_key": "object_type", "value": "AX_KommunalesGebiet" }
            }
        }
    }',
    'Im Bereich der Gebietsgrenze stimmt der Verlauf nicht mit den Kommunalen Gebieten überein.')
ON CONFLICT (id) DO NOTHING;
