/*******************************************************
 CS451 Multimedia Software Systems
 *******************************************************/

// import all homework assignments
import ctrl.*;
import views.*;
import models.*;
import test.*;

// using for unit test
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class CS451_Test {
	public static final boolean DEV_OPTION = true;

	public static void main(String[] args) {
		int fails = 0;
		int pass = 0;

		if (DEV_OPTION) {
			Result result = JUnitCore.runClasses(ModelSuite.class);
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.getTrace());
			}
			System.out.println("Number of test cases: " + result.getRunCount());
			System.out.println("Number of failures: " + result.getFailureCount());
			System.out.println("All test cases passed: " + result.wasSuccessful());
		}
	}
}