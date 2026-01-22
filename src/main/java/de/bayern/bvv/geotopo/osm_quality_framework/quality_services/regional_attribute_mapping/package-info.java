@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: *",
                "quality_core :: *",
                "openstreetmap_schema :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.regional_attribute_mapping;