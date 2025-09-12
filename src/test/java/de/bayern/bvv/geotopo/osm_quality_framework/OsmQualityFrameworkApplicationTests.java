package de.bayern.bvv.geotopo.osm_quality_framework;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
class OsmQualityFrameworkApplicationTests {

	ApplicationModules applicationModules = ApplicationModules.of(OsmQualityFrameworkApplication.class);

	@Test
	void contextLoads() {
		this.applicationModules.verify();
	}

	@Test
	void writeDocumentation() {
		new Documenter(this.applicationModules)
				.writeModulesAsPlantUml()
				.writeIndividualModulesAsPlantUml();
	}

}
