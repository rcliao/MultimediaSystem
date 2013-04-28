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

		if (DEV_OPTION) {
			Result result = JUnitCore.runClasses(UnitTest.class);
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}
			System.out.println(result.wasSuccessful());
		}
	}
}