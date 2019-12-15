

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gpio.DigicodeTest;
import gpio.SensorTest;

@RunWith(Suite.class)
@SuiteClasses({ DigicodeTest.class, SensorTest.class })
public class GPIOTests {

}
