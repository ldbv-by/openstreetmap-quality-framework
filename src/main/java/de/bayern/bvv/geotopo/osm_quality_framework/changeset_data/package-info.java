@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: changeset-dto",
                "quality_core :: changeset-mapper",
                "quality_core :: changeset-model"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.changeset_data;