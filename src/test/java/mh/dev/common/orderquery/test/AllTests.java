package mh.dev.common.orderquery.test;

import mh.dev.common.orderquery.test.tests.AnnotationModelColumnListOrderListTests;
import mh.dev.common.orderquery.test.tests.AnnotationModelColumnListOrderMapTests;
import mh.dev.common.orderquery.test.tests.AnnotationModelOrderStateTests;
import mh.dev.common.orderquery.test.tests.MixedModelColumnListOrderListTests;
import mh.dev.common.orderquery.test.tests.MixedModelColumnListOrderMapTests;
import mh.dev.common.orderquery.test.tests.MixedModelOrderStateTests;
import mh.dev.common.orderquery.test.tests.XmlModelColumnListOrderListTests;
import mh.dev.common.orderquery.test.tests.XmlModelColumnListOrderMapTests;
import mh.dev.common.orderquery.test.tests.XmlModelOrderStateTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AnnotationModelOrderStateTests.class, MixedModelOrderStateTests.class, XmlModelOrderStateTests.class,
		AnnotationModelColumnListOrderMapTests.class, MixedModelColumnListOrderMapTests.class, XmlModelColumnListOrderMapTests.class,
		AnnotationModelColumnListOrderListTests.class, MixedModelColumnListOrderListTests.class, XmlModelColumnListOrderListTests.class })
public class AllTests {

}
