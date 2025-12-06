@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {
                "quality_services :: *",
                "rule_engine :: *",
                "openstreetmap_schema :: *",
                "quality_core :: *"
        }
)
package de.bayern.bvv.geotopo.osm_quality_framework.quality_services.attribute_check;