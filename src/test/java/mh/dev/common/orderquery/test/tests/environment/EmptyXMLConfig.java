package mh.dev.common.orderquery.test.tests.environment;

import java.io.File;

import javax.ejb.EJB;

import mh.dev.common.orderquery.core.exception.OrderQueryException;
import mh.dev.common.orderquery.test.core.ExceptionCauseTraceMatcher;
import mh.dev.common.orderquery.test.core.OrderStateExceptionService;
import mh.dev.common.util.xml.exception.UnmarshalFailedException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EmptyXMLConfig {

	@EJB
	private OrderStateExceptionService orderStateExceptionService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String generatedPomLocation = "build/poms/pom-default.xml";

	@Deployment
	public static Archive<?> createDeployment() {
		File[] libraries = Maven.resolver().offline().loadPomFromFile(new File(generatedPomLocation)).importRuntimeDependencies().resolve().withTransitivity()
				.asFile();
		return ShrinkWrap.create(WebArchive.class, "orderQuery.war").addPackages(true, "mh.dev.common.orderquery").addAsLibraries(libraries)
				.addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsResource("orderquery/empty.xml", "META-INF/orderquery.xml");

	}

	@Test
	public void emptyXmlConfig() {
		thrown.expect(new ExceptionCauseTraceMatcher(OrderQueryException.class, UnmarshalFailedException.class));
		orderStateExceptionService.getOrderState();

	}
}
