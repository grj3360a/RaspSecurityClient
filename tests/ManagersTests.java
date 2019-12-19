import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import managers.DatabaseTest;
import managers.NotifManagerTest;
import managers.RestAPITest;
import managers.SecuTest;

@RunWith(Suite.class)
@SuiteClasses({ RestAPITest.class, DatabaseTest.class, SecuTest.class, NotifManagerTest.class })
public class ManagersTests {}
