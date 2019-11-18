package CTD_E2E;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Properties;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_ContractTests.Adapter;
import CTD_ContractTests.At_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Dynamics.dynamics;
import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class act extends extentreport {

	private String username;
	private String password;
	private Connection conn;
	private FileInputStream fis;
	private Properties prop = new Properties();
	private String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	private delete_events deleteevent = new delete_events();

	At_Event_verify_event unprocessed = new At_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();

	public String access_token;
	public dynamics dy = new dynamics();

	@BeforeClass
	private void jdbc() throws SQLException, ClassNotFoundException {
		try {

			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	/**
	 * 
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException 
	 */

	@Test()
	public void codeanddescriptionchanges() throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {

		System.out.println(prop.getProperty("act_run_status"));

		Statement s = conn.createStatement();
		Statement f = conn.createStatement();

		Markup m;

		int count = 0;
		int maxtries = 2;

		True: while (true) {
			ResultSet rs = null;
			ResultSet rsup = null;
			ResultSet rsupcd = null;

			String code = null;
			String description = null;
			String TIMEST = null;
			String TYPE = prop.getProperty("act_TYPE");

			String gd = null;
			try {

				parentTest = extent.createTest(prop.getProperty("act_parent_test"));

				parentTest.assignCategory(prop.getProperty("at_event_category"));

				childTest = parentTest.createNode(prop.getProperty("act_child_onetest"));

				s.executeQuery(prop.getProperty("act_cdanddscchanges_first_insert"));
				conn.commit();
				
				Thread.sleep(120000L);

				rs = s.executeQuery(prop.getProperty("act_cdanddscchanges_first_select"));

				if (rs.next()) {
					code = rs.getString("code");
					description = rs.getString("description");
					TIMEST = rs.getString("TIMEST");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
					String[][] requestchosen = { { "CODE", "DESC", "TIMEST" }, { code, description, TIMEST } };
					m = MarkupHelper.createTable(requestchosen);

					childTest.log(Status.INFO, m);

					verifyNameInDynamics(access_token);

					deleteevent.delete();

				}

			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("act_catch_delete"));
				conn.commit();
				deleteevent.delete();
				if (++count == maxtries)
					throw e;

			}
		}
	}

	public void verifyNameInDynamics(String access_token) throws InterruptedException {
		int count = 0;
		int maxtries = 5;
		String firstName = null;
		String lastName = null;
		String edpt = prop.getProperty("e2e_act_dyedpt").replaceAll("\\{id\\}", "MAN");

		exit: while (true) {

			Response res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
					"Accept", ContentType.JSON).when().get(edpt).then().extract().response();
			String response = res.asString();
			System.out.println(response);

			int Status_code = res.getStatusCode();
			System.out.println(Status_code);

			if (Status_code != 200) {
				++count;

				switch (count) {
				case 1:
					Thread.sleep(10000L);
					break;

				case 2:
					Thread.sleep(20000L);
					break;

				case 3:
					Thread.sleep(30000L);
					break;

				case 4:
					Thread.sleep(40000L);
					break;
				}

				if (count == maxtries) {
					parentTest.log(Status.ERROR, "Statuscode: " + String.valueOf(Status_code));
					throw new SkipException(
							"waited for 100 seconds and could not get a 200 status code from Dynamics. Please verify the problem and complete the test");
				}

			}
		}

	}

	@AfterMethod
	public void tearDown(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			parentTest.log(Status.FAIL,
					"Test Fail: Log defect <a href=\"http://abcd.efg.com\" target=\"_blank\">Log Defect</a>");
			parentTest.fail(result.getThrowable());

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			parentTest.log(Status.PASS, MarkupHelper.createLabel("Test passed", colour.GREEN));
		} else if (result.getStatus() == ITestResult.SKIP) {
			parentTest.log(Status.PASS, MarkupHelper.createLabel("Test skipped", colour.YELLOW));
			parentTest.log(Status.SKIP, result.getThrowable());
		}
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
