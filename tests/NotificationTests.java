import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import notification.FreeAPITest;
import notification.IFTTTTest;
import notification.NotifManagerTest;

@RunWith(Suite.class)
@SuiteClasses({ NotifManagerTest.class, FreeAPITest.class, IFTTTTest.class })
public class NotificationTests {}
