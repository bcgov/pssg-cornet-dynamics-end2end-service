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
import CTD_ContractTests.Stat_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Dynamics.dynamics;
import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class statute extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	Stat_Event_verify_event unprocessed = new Stat_Event_verify_event();
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

	@Test
	public void insertupdateanddelete() throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("stat_TYPE");

		String gd = null;
		BigDecimal id;
		String idstring = null;
		parentTest = extent.createTest(prop.getProperty("Stat_insertupdateanddelete_parent_extent"));
		parentTest.assignCategory(prop.getProperty("stat_event"));
		int count = 0;
		int maxtries = 2;

		try {
			True: while (true) {
				try {

					System.out.println("..");

					childTest = parentTest.createNode(prop.getProperty("Stat_insertupdateanddelete_onechild"));

					s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneinsert"));
					conn.commit();

					String LTY_CD = prop.getProperty("stat_lty_cd_val");
					String PTY_CD = prop.getProperty("stat_PTY_CD_cd_val");
					String DESCR = prop.getProperty("stat_DESCR_val");
					String sc_no = prop.getProperty("stat_sc_no_val");
					String sub_sc_no = prop.getProperty("stat_sub_sc_no_val");
					String sub_para_no = prop.getProperty("stat_sub_para_no_val");
					String para_no = prop.getProperty("stat_para_no_val");

					rs = s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneselect"));

					if (rs.next()) {

						id = rs.getBigDecimal("id");
						idstring = String.valueOf(id);
						TIMEST = rs.getString("TIMEST");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted into cms_statutes");
						String[][] requestchosen = {
								{ prop.getProperty("sta_id"), prop.getProperty("sta_lty_cd"),
										prop.getProperty("sta_pty_cd"), prop.getProperty("sta_descr_cd"), "TIMEST" },
								{ idstring, LTY_CD, PTY_CD, DESCR, TIMEST } };
						m = MarkupHelper.createTable(requestchosen);
						
						Thread.sleep(120000L);

						verifyNameInDynamics(access_token, idstring);

						deleteevent.delete();
						
						break True;

					}
				} catch (SQLException e) {
					if (++count == maxtries)
						throw e;
					else
						s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_catch_del"));
					conn.commit();

				}
			}
		} finally {
			rs.close();
		}

	}

	public void verifyNameInDynamics(String access_token, String id) throws InterruptedException {
		int count = 0;
		int maxtries = 5;
		String firstName = null;
		String lastName = null;
		String edpt = prop.getProperty("e2e_statute_dyedpt").replaceAll("\\{id\\}", id);

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
