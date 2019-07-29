package DTC_E2E;

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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import CTD_ContractTests.dynamics;
import CTD_ContractTests.inscscliefn;
import CTD_Extent_Report.extentreport;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class cl extends extentreport{

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	dynamics dy = new dynamics();
	inscscliefn ins=new inscscliefn();

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
	public void dynamicstocornet() throws ClientProtocolException, IOException, SQLException, InterruptedException, ClassNotFoundException {
		test=extent.createTest("Dynamics to Cornet");
		test.assignCategory("Dynamics to CORNET");
		
		String access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();
		ins.insert_into_csclie();
		int i=1;
		String CNO=null;
		BigDecimal id=null;
		
		Statement s=conn.createStatement();
		ResultSet rs=null;
		ResultSet clieid=null;
		String query=null;
		String payload=null;
		String idstring=null;
		String clielogid=null;
		
		rs=s.executeQuery(prop.getProperty("lp_insert_oneselect").replaceAll("\\{fc\\}", String.valueOf(i)));
		
		if(rs.next())
		{
			CNO=rs.getString("cno");
			id=rs.getBigDecimal("id");
			idstring=String.valueOf(id);
			
			query=prop.getProperty("tlinsertcl_insert_csclienames_onehalf")+" "+id+" "+prop.getProperty("tlinsertcl_insert_csclienames_twohalf");
			s.executeQuery(query);
			conn.commit();
			
			payload=prop.getProperty("dtc_payload").replaceAll("\\{CNO\\}", CNO);
			payload=payload.replaceAll("\\{escapeone\\}", "\\\\");
			payload=payload.replaceAll("\\{escapetwo\\}", "\\\\");
			
			//System.out.println(payload);
			
			Thread.sleep(40000L);
			
			given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON)
			.body(payload).when().post(prop.getProperty("dtc_edpt")).then().assertThat()
			.statusCode(204);
			
			Thread.sleep(40000L);
			
			clieid=s.executeQuery(prop.get("dtc_select")+""+idstring+"");
			
			if(clieid.next())
			{
				clielogid=clieid.getString("clielogid");
				test.log(Status.INFO, "The id of the created record is "+""+clielogid+"");
				
				
			}
			else
			{
				Assert.assertFalse(false);
			}
			
		}	
		
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
