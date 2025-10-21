package de.bayern.bvv.geotopo.osm_quality_framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulithic;

@Modulithic(additionalPackages = "de.bayern.bvv.geotopo.osm_quality_framework.quality_services")
@EnableCaching
@SpringBootApplication
public class OsmQualityFrameworkApplication {
	public static void main(String[] args) {
		SpringApplication.run(OsmQualityFrameworkApplication.class, args);
	}
}
