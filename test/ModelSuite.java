/**
 * Test Suite class
 */

package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// connection all the test class to this suite
@RunWith(Suite.class)
@Suite.SuiteClasses({
   TextModelTest.class,
   ImageModelTest.class
})

public class ModelSuite {   
}