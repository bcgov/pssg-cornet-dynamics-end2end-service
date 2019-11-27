package DTC_clientlogs;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.http.client.ClientProtocolException;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_Dynamics.dynamics;
import CTD_ContractTests.inscscliefn;
import CTD_Extent_Report.extentreport;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

public class creatiaform extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	dynamics dy = new dynamics();
	inscscliefn ins = new inscscliefn();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException, IOException {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test
	public void dynamicstocornet()
			throws ClientProtocolException, IOException, SQLException, InterruptedException, ClassNotFoundException {
		test = extent.createTest("Dynamics to Cornet");
		test.assignCategory("Dynamics to CORNET");

		String access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();
		ins.insert_into_csclie();
		int i = 1;
		String CNO = null;
		BigDecimal id = null;

		Statement s = conn.createStatement();
		ResultSet rs = null;
		ResultSet clieid = null;
		String query = null;
		String payload = null;
		String idstring = null;
		String clielogid = null;

		rs = s.executeQuery(prop.getProperty("lp_insert_oneselect").replaceAll("\\{fc\\}", String.valueOf(i)));

		if (rs.next()) {
			CNO = rs.getString("cno");

			id = rs.getBigDecimal("id");

			idstring = String.valueOf(id);

			query = prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + " " + id + " "
					+ prop.getProperty("tlinsertcl_insert_csclienames_twohalf");

			s.executeQuery(query);

			conn.commit();

			payload = prop.getProperty("createiaformdypayload").replaceAll("\\{CNO\\}", CNO);

			payload = payload.replaceAll("\\{escapeone\\}", "\\\\");

			payload = payload.replaceAll("\\{escapetwo\\}", "\\\\");

			Thread.sleep(100000L);

			given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON).body(payload)
					.when().post(prop.getProperty("dtc_iaedpt")).then().assertThat().statusCode(204);

			Thread.sleep(100000L);

			//String dtc_cornetiaedpt = prop.getProperty("dtc_cornetiaedpt").replaceAll("\\{CNO\\}", CNO);

			given().header("Content-Type", "Application/json").queryParam("client_no", CNO).when()
					.get(prop.getProperty("dtc_cornetiaedpt")).then().assertThat().statusCode(200)
					.body(prop.getProperty("iapop"), is("GP SEG ESP")).body(prop.getProperty("iaclass"), is("Medium"));

		}

	}

	@AfterMethod
	public void tearDown(ITestResult result) {

		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL,
					"Test Fail: Log defect <a href=\"http://abcd.efg.com\" target=\"_blank\">Log Defect</a>");
			test.fail(result.getThrowable());

		} else if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, MarkupHelper.createLabel("Test passed", ExtentColor.GREEN));
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.PASS, MarkupHelper.createLabel("Test skipped", ExtentColor.YELLOW));
			test.log(Status.SKIP, result.getThrowable());
		}
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
