@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_core :: object-type-model",
                "quality_core :: object-type-dto",
                "quality_core :: object-type-mapper",
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.openstreetmap_schema;