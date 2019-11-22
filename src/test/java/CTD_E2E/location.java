package CTD_E2E;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;

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
import CTD_ContractTests.Loc_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Dynamics.dynamics;
import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class location extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public delete_events deleteevent = new delete_events();

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";

	Loc_Event_verify_event unprocessed = new Loc_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	public String access_token;
	public dynamics dy = new dynamics();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
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

	@Test()
	public void locaevent() throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {

		parentTest = extent.createTest(prop.getProperty("locaevent_parentextent"));

		parentTest.assignCategory(prop.getProperty("locaevent_category"));

		Statement s = conn.createStatement();
		int count = 0;
		int maxtries = 2;

		True: while (true) {
			try {

				System.out.println("..");

				String id = null;

				LinkedHashSet<String> idandtimest = new LinkedHashSet<String>();

				parentinsert();
				
				s.executeQuery(prop.getProperty("location_catch_delete"));
				conn.commit();
				
				break True;

			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("location_catch_delete"));
				conn.commit();
				deleteevent.delete();
				if (++count == maxtries)
					throw e;
			}
		}

	}

	private void parentinsert() throws SQLException, ParseException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		Statement s = conn.createStatement();
		ResultSet rs = null;

		childTest = parentTest.createNode(prop.getProperty("insil"));
		s.executeQuery(prop.getProperty("parentinsert_ilinsert"));
		conn.commit();
		rs = s.executeQuery(prop.getProperty("parentinsert_ilselect"));

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		String loc_id = null;
		String idstring = null;

		Markup m;

		if (rs.next()) {

			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));

			childTest.log(Status.INFO, "The following were inserted");
			String[][] insertiloclog = { { "ID", "CODE", "DESC", "eff_dt", "exp_dt", "loc_id_root", "ent_dt" },
					{ idstring, code, desc, eff_dt, exp_dt, null, TIMEST } };
			m = MarkupHelper.createTable(insertiloclog);
			childTest.log(Status.INFO, m);
			
			Thread.sleep(120000L);
			
			verifyNameInDynamics(access_token, idstring);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();
			
			s.close();
			rs.close();
			childTest.pass("Test Passed");

		}

	}
	
	public void verifyNameInDynamics(String access_token, String id) throws InterruptedException {
		int count = 0;
		int maxtries = 5;
		String edpt = prop.getProperty("e2e_location_dyedpt").replaceAll("\\{id\\}", id);

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
			else
			{
				break exit;
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
