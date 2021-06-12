package eu.assina.app;

import eu.assina.app.api.ApiSuite;
import eu.assina.app.csc.CSCSuite;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({ApiSuite.class, CSCSuite.class })
public class AllTests {
}
