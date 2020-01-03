import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import managers.DatabaseTest;
import managers.NotifManagerTest;
import managers.RestAPITest;

@RunWith(Suite.class)
@SuiteClasses({ RestAPITest.class, DatabaseTest.class, NotifManagerTest.class })
public class ManagersTests {
}
