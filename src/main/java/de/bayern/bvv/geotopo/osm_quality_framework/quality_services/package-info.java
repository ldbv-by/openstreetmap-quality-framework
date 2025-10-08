@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: changeset-dto",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-model",
                "quality_core :: feature-dto",
                "quality_core :: feature-mapper",
                "quality_core :: feature-model",
                "quality_core :: object-type-dto",
                "quality_core :: object-type-mapper",
                "quality_core :: object-type-model",
                "rule_engine :: api",
                "rule_engine :: parser",
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services;