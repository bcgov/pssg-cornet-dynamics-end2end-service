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

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_ContractTests.Adapter;
import CTD_ContractTests.delete_events;
import CTD_Dynamics.dynamics;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class tombstone extends extentreport{
	
	public String username;
	public String password;
	public Connection conn;
	public String access_token;
	public String edpt;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc = new inscscliefn();
	
	public dynamics dy=new dynamics();
	public Dynamics_val dval=new Dynamics_val();


	Markup m;
	
	@BeforeTest()
	public void connection()
	{
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			access_token=dy.whenPostJsonUsingHttpClient_thenCorrect();
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}
	
	@Test(groups="smoke")
	public void tombstone() throws SQLException, InterruptedException, ClassNotFoundException, ParseException, IOException
	{
		parentTest = extent.createTest("Tombstone Event");
		parentTest.assignCategory("End to End");
		
		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		insc.insert_into_csclie();
		
		rs = s.executeQuery(prop.getProperty("tinsertcl_oneselect"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}
		
		Thread.sleep(120000L);
		
		deleteevent.delete();
		
		insert_csclienames(idstring, CNO );
		
		
	}
	
	private void insert_csclienames(String idstring, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet clinsert = null;
		BigDecimal id = null;
		String clieidstring = null;
		String TIMEST = null;

		try {

			try {
				t.executeQuery(prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + "" + idstring + ""
						+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf"));
				conn.commit();
			} catch (SQLException e) {
				throw new SkipException("Trouble with SQL Triggers- client_name");
			}

			clinsert = t.executeQuery(prop.getProperty("tlinsertcl_oneselect_onehalf") + " " + idstring + " "
					+ prop.getProperty("tlinsertcl_oneselect_twohalf"));

			if (clinsert.next()) {
				id = clinsert.getBigDecimal("id");
				clieidstring = String.valueOf(id);
				TIMEST = clinsert.getString("TIMEST");

				String[][] responsetable = { { "id", "clie_id", "ent_dt" }, { clieidstring, idstring, TIMEST } };
				m = MarkupHelper.createTable(responsetable);
				parentTest.log(Status.INFO, m);
				
				Thread.sleep(120000L);

				verifyNameInDynamics(access_token, CNO, prop.getProperty("tstone_fname_val"), prop.getProperty("tstone_lname_val"));

				deleteevent.delete();

			}
		} finally {
			clinsert.close();
			t.close();

		}

	}
	
	public void verifyNameInDynamics(String access_token, String CNO, String firstNameValue, String lastNameValue)
			throws InterruptedException {
		int count = 0;
		int maxtries = 5;
		String firstName = null;
		String lastName = null;
		String edpt= prop.getProperty("lp_insert_dy_edpt").replaceAll("\\{CNO\\}", CNO);

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

			} else {
				JsonPath js = new JsonPath(response);

				
					firstName = js.get(prop.getProperty("tstone_fname"));
					lastName=js.get(prop.getProperty("tstone_lname"));
					
					break exit;
					
			}
		}

		if (firstName != null) {
			Assert.assertEquals(firstName, firstNameValue, "validation to verify that the changes were updated on Dynamics");
		} else {
			parentTest.log(Status.INFO, "Element Key not found in dynamics response");
			Assert.assertTrue(false, "Waited 2 mins and Element Key not found in dynamics response");
		}
		if (lastName != null) {
			Assert.assertEquals(lastName, lastNameValue, "validation to verify that the changes were updated on Dynamics");
		} else {
			parentTest.log(Status.INFO, "Element Key not found in dynamics response");
			Assert.assertTrue(false, "Waited 2 mins and Element Key not found in dynamics response");
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
