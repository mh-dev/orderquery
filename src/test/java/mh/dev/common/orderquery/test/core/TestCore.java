package mh.dev.common.orderquery.test.core;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class TestCore {

	/**
	 * Execute gradle install if this file does not exist
	 */
	private static final String generatedPomLocation = "build/poms/pom-default.xml";

	@Deployment
	public static Archive<?> createDeployment() {
		File[] libraries = Maven.resolver().offline().loadPomFromFile(new File(generatedPomLocation)).importRuntimeDependencies().resolve().withTransitivity()
				.asFile();
		return ShrinkWrap.create(WebArchive.class, "orderQuery.war").addPackages(true, "mh.dev.common.orderquery").addAsLibraries(libraries)
				.addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
}
