package utils;

import java.util.Arrays;
import java.util.List;

public class JUnitUtil {
	
	/**
	 * This method must not be used to alter code utilization<br>
	 * For the moment, this method is only used to change RestAPI port to remove the JVM_BIND error.
	 * @return If running on JUnit
	 */
	public static boolean isJUnitTest() {
	    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	    List<StackTraceElement> list = Arrays.asList(stackTrace);
	    for (StackTraceElement element : list) {
	        if (element.getClassName().startsWith("org.junit.")) {
	            return true;
	        }           
	    }
	    return false;
	}
	
}
