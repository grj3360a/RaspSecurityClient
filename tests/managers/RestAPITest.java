package managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.managers.NotificationManager;
import me.security.managers.RestAPIManager;
import me.security.managers.SecuManager;
import me.security.simulation.SimulatedMode;
import utils.JUnitGPIO;
import utils.dummy.DummyDatabaseManager;

public class RestAPITest {

	private static SimulatedGpioProvider gpio;
	private static String jUnitAppPassword;

	@BeforeClass
	public static void setUpClass() throws Exception {
		jUnitAppPassword = "eaz897hfg654kiu714sf32d1";
		SimulatedMode.setup();
	}

	@AfterClass
	public static void tearDownClass() {
		JUnitGPIO.cleanOut(gpio);
		gpio = null;
	}

	//

	private HttpClient httpClient;
	private SecuManager secu;
	private RestAPIManager restapi;

	@Before
	public void setUp() throws Exception {
		// Get the RestAPI from a dummy SecurityManager
		this.secu = new SecuManager(new NotificationManager(), new DummyDatabaseManager());
		this.restapi = this.secu.getRestApi();
		// We need a HttpClient to create GET request
		this.httpClient = HttpClientBuilder.create().build();
	}

	@After
	public void tearDown() throws Exception {
		assertNotNull(this.restapi);
		this.restapi.close();
		this.restapi = null;
		JUnitGPIO.cleanOut(gpio);
	}

	@Test
	public void testNoAuth() throws Exception {
		HttpGet request = new HttpGet("http://127.0.0.1:" + RestAPIManager.PORT + "/alarm");
		HttpResponse hr = this.httpClient.execute(request);
		assertEquals(401, hr.getStatusLine().getStatusCode());
	}

	@Test
	public void testInvalidAuth() throws Exception {
		HttpGet request = new HttpGet("http://127.0.0.1:" + RestAPIManager.PORT + "/alarm");
		request.addHeader("appPassword", "INVALID_PASSWORD");
		HttpResponse hr = this.httpClient.execute(request);
		assertEquals(401, hr.getStatusLine().getStatusCode());
	}

	@Test
	public void testAlarmState() throws Exception {
		HttpGet request = new HttpGet("http://127.0.0.1:" + RestAPIManager.PORT + "/alarm");
		request.addHeader("appPassword", jUnitAppPassword);
		HttpResponse hr = this.httpClient.execute(request);
		assertEquals(200, hr.getStatusLine().getStatusCode());
		assertEquals("false", IOUtils.toString(new InputStreamReader(hr.getEntity().getContent())).trim());
	}

	@Test
	public void testAlarmToggle() throws Exception {
		System.out.println("http://127.0.0.1:" + RestAPIManager.PORT + "/alarm/toggle");
		HttpGet request = new HttpGet("http://127.0.0.1:" + RestAPIManager.PORT + "/alarm/toggle");
		request.addHeader("appPassword", jUnitAppPassword);

		// Toggle ON
		HttpResponse hrOnToggle = this.httpClient.execute(request);
		assertEquals(200, hrOnToggle.getStatusLine().getStatusCode());
		assertEquals("true", IOUtils.toString(new InputStreamReader(hrOnToggle.getEntity().getContent())).trim());

		// Toggle OFF on the same object
		HttpResponse hrOffToggle = this.httpClient.execute(request);
		assertEquals(200, hrOffToggle.getStatusLine().getStatusCode());
		assertEquals("false", IOUtils.toString(new InputStreamReader(hrOffToggle.getEntity().getContent())).trim());
	}
}
