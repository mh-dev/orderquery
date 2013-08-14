package mh.dev.common.orderquery.test;

import mh.dev.common.orderquery.test.tests.AnnotationModelOrderStateTests;
import mh.dev.common.orderquery.test.tests.MixedModelOrderStateTests;
import mh.dev.common.orderquery.test.tests.XmlModelOrderStateTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AnnotationModelOrderStateTests.class, MixedModelOrderStateTests.class, XmlModelOrderStateTests.class })
public class AllTests {

}
