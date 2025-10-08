@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: changeset-dto",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-model",
                "quality_core :: feature-dto",
                "quality_core :: feature-mapper",
                "quality_core :: feature-model"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services;