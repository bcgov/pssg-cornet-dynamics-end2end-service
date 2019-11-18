package CTD_ContractTests;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_Extent_Report.extentreport;
import io.restassured.response.Response;

public class personalxref extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	// public String access_token;
	public String edpt;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc = new inscscliefn();

	@BeforeTest()
	public void connection() {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// access_token=dy.whenPostJsonUsingHttpClient_thenCorrect();
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test
	public void insertcode() throws SQLException, ClassNotFoundException, InterruptedException {
		Statement s = conn.createStatement();
		int count = 0;
		int maxtries = 4;

		test = extent.createTest("Personal Xref Event");

		exit: while (true) {
			try {
				s.executeQuery(prop.getProperty("zabinsert"));
				conn.commit();
				break exit;
			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("zabdeletecdo"));
				conn.commit();
				s.executeQuery(prop.getProperty("zabdeletecd"));
				conn.commit();

				if (count++ == maxtries)
					throw e;
			}
		}

		count = 0;
		exitlab: while (true) {
			try {
				s.executeQuery(prop.getProperty("labinsert"));
				conn.commit();
				break exitlab;
			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("labdeletecdo"));
				conn.commit();
				s.executeQuery(prop.getProperty("labdeletecd"));
				conn.commit();

				if (count++ == maxtries)
					throw e;
			}
		}

		deleteevent.delete();

		s.executeQuery(prop.getProperty("cdooneinsert"));
		conn.commit();
		s.executeQuery(prop.getProperty("cdoeuinsert"));
		conn.commit();
		s.executeQuery(prop.getProperty("cdoocinsert"));
		conn.commit();
		s.executeQuery(prop.getProperty("cdoeminsert"));
		conn.commit();
		s.executeQuery(prop.getProperty("cdomarinsert"));
		conn.commit();

		Set<String> domaincode = new HashSet<String>();

		Set<String> validdomaincode = new HashSet<String>();

		validdomaincode.add(prop.getProperty("rccode"));
		validdomaincode.add(prop.getProperty("eucode"));
		validdomaincode.add(prop.getProperty("occode"));
		validdomaincode.add(prop.getProperty("emcode"));
		validdomaincode.add(prop.getProperty("marcode"));

		ResultSet rs = s.executeQuery(prop.getProperty("selectmessageelt"));
		while (rs.next()) {
			domaincode.add(rs.getString("dvt"));
		}

		System.out.println(domaincode);

		System.out.println(validdomaincode);

		Assert.assertTrue(domaincode.containsAll(validdomaincode));

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatedsc"));
		conn.commit();

		domaincode = new HashSet<String>();
		count = 0;

		elt: while (true) {

			rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

			if (rs.next()) {

				rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

				while (rs.next()) {
					if (rs.getString("dvt") != null) {
						domaincode.add(rs.getString("dvt"));
					}
				}
				break elt;
			} else {
				Thread.sleep(40000L);
				if (count++ == maxtries)
					Assert.assertTrue(false, "Cannot find event");
			}
		}

		System.out.println(domaincode);

		System.out.println(validdomaincode);

		Assert.assertTrue(domaincode.containsAll(validdomaincode));

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updateeftdt"));
		conn.commit();

		domaincode = new HashSet<String>();
		count = 0;

		elt: while (true) {

			rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

			if (rs.next()) {

				rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

				while (rs.next()) {
					if (rs.getString("dvt") != null) {
						domaincode.add(rs.getString("dvt"));
					}
				}
				break elt;
			} else {
				Thread.sleep(40000L);
				if (count++ == maxtries)
					Assert.assertTrue(false, "Cannot find event");
			}
		}

		System.out.println(domaincode);

		System.out.println(validdomaincode);

		Assert.assertTrue(domaincode.containsAll(validdomaincode));

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updateexpiry"));
		conn.commit();

		domaincode = new HashSet<String>();
		count = 0;

		elt: while (true) {

			rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

			if (rs.next()) {

				rs = s.executeQuery(prop.getProperty("selectmessageeltnotnull"));

				while (rs.next()) {
					if (rs.getString("dvt") != null) {
						domaincode.add(rs.getString("dvt"));
					}
				}
				break elt;
			} else {
				Thread.sleep(40000L);
				if (count++ == maxtries)
					Assert.assertTrue(false, "Cannot find event");
			}
		}

		System.out.println(domaincode);

		System.out.println(validdomaincode);

		Assert.assertTrue(domaincode.containsAll(validdomaincode));

		deleteevent.delete();

		s.executeQuery(prop.getProperty("updatecty"));
		conn.commit();

		rs = s.executeQuery(prop.getProperty("selectmessageelt"));

		domaincode = new HashSet<String>();

		while (rs.next()) {
			domaincode.add(rs.getString("dvt"));
		}

		System.out.println(domaincode);

		System.out.println(validdomaincode);

		Assert.assertTrue(domaincode.containsAll(validdomaincode));

	}

	@Test
	public void verifyords() {

		test = extent.createTest("xref_ords");

		String rccode = prop.getProperty("rccode");
		Response res = given().header("Content-Type", "Application/json").queryParam("pcdo_cd", "EMPS").and()
				.queryParam("pcty_cd", "JABO").when().get(prop.getProperty("or_edpt_xref")).then().assertThat()
				.statusCode(200).extract().response();

		ArrayList<Map<String, ?>> orget = res.path(prop.getProperty("or_root"));

		Map<String, ?> map = orget.get(0);

		Assert.assertEquals("Labbi", map.get(prop.getProperty("chardesc")));
		Assert.assertEquals("2016-06-04", map.get(prop.getProperty("eff_dt")));
		Assert.assertEquals("2019-06-04", map.get(prop.getProperty("exp_dt")));

		// System.out.println(res.asString());

	}

	@Test
	public void verifyadapter() {

		test = extent.createTest("xref_adapter");

		String rccode = prop.getProperty("rccode");
		Response res = given().header("Content-Type", "Application/json").body(prop.getProperty("da_xref_payload"))
				.when().post(prop.getProperty("da_xref_edpt")).then().assertThat().statusCode(200)
				.body(prop.getProperty("stat"), is(204)).body(prop.getProperty("disc_chg_reg_dy_py_cd"), is("4"))
				.body(prop.getProperty("dy_sg_type"), is("Grade 10 or 11"))
				.body(prop.getProperty("dy_py_sta_eff_dt"), is("1997-06-16"))
				.body(prop.getProperty("dy_py_sta_exp_dt"), is("2018-04-20")).extract().response();

	}

	@AfterMethod
	public void tearDown(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL,
					"Test Fail: Log defect <a href=\"http://abcd.efg.com\" target=\"_blank\">Log Defect</a>");
			test.fail(result.getThrowable());

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, MarkupHelper.createLabel("Test passed", colour.GREEN));
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.PASS, MarkupHelper.createLabel("Test skipped", colour.YELLOW));
			test.log(Status.SKIP, result.getThrowable());
		}
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
