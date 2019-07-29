package CTD_E2E;

import static io.restassured.RestAssured.given;

import static org.testng.Assert.fail;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.jayway.jsonpath.internal.JsonFormatter;
import com.mongodb.util.JSON;

import CTD_ContractTests.delete_events;
import CTD_ContractTests.dynamics;
import CTD_ContractTests.inscscliefn;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import CTD_Extent_Report.extentreport;
import groovy.transform.builder.InitializerStrategy.SET;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class peract extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public String Filechart = null;
	public delete_events deleteevent = new delete_events();
	public inscscliefn insc = new inscscliefn();
	public XSSFWorkbook workbook;
	public XSSFSheet sheet;
	public Iterator<Row> rows;
	public int rownum = 0;
	public Row row;
	chart ch = new chart();
	perfchart pc = new perfchart();
	align an = new align();
	dynamics dy = new dynamics();
	delete_events delete = new delete_events();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException, IOException {
		try {
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet("chart");
			rows = sheet.iterator();
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test()
	public void insert()
			throws ClassNotFoundException, SQLException, IOException, InterruptedException, ParseException {

		parentTest = extent.createTest("load");
		childTest = parentTest.createNode("perft");

		CellStyle style = workbook.createCellStyle();
		// Setting Background color
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sheet.createRow(0).createCell(0).setCellStyle(style);
		sheet.getRow(0).getCell(0).setCellValue("User No");
		sheet.getRow(0).createCell(1).setCellStyle(style);
		sheet.getRow(0).getCell(1).setCellValue("Response_Time");
		sheet.getRow(0).createCell(2).setCellStyle(style);
		sheet.getRow(0).getCell(2).setCellValue("User_Action");

		LinkedHashSet<String> code = execact();

		Thread.sleep(1520000L);

		accode(code);

		String datetime = pc.gettimest();
		Filechart = System.getProperty("user.dir") + "/Chart/chart_" + "" + datetime + "" + ".xlsx";
		FileOutputStream os = new FileOutputStream(Filechart);
		workbook.write(os);
		align an = new align();
		// System.out.println("chart_"+datetime);
		an.alpng("chart_" + datetime);
	}

	private void accode(LinkedHashSet<String> code) throws SQLException, InterruptedException, ParseException, ClientProtocolException, IOException {
		// TODO Auto-generated method stub

		Iterator<String> it = code.iterator();
		String cd = null;

		String createdtime = null;

		ResultSet perftstoneins = null;
		Statement ptstoneins = conn.createStatement();
		// Iterator<String> it = tstoneup_cno.iterator();
		String TIMEST = null;
		int i = 1;
		int count=1;
		int maxtries=4;

		while (it.hasNext()) {
			cd = it.next();


			exit:
				while(true)
				{
				
				try {
					perftstoneins = ptstoneins.executeQuery(prop.getProperty("tstoneup_cnoselect") + "'" + cd + "'");
					perftstoneins.next();

					TIMEST = perftstoneins.getString("TIMEST");

					System.out.println(TIMEST);
					break exit;
				}
				catch(SQLException e)
				{
					Thread.sleep(20000L);
					if (++count == maxtries)
						throw e;
				}
				}

			String access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();

			createdtime = createdtime(access_token, cd, createdtime);

			String Perftime = Perftime(createdtime);

			double val = responsetimedifference(TIMEST, Perftime);

			rownum = sheet.getLastRowNum();
			row = sheet.createRow(rownum + 1);
			int n = i;
			row.createCell(0).setCellValue(n);
			row.createCell(1).setCellValue(val);
			i++;

		}

	}

	private LinkedHashSet<String> execact() throws SQLException {
		// TODO Auto-generated method stub

		Statement s = conn.createStatement();
		ResultSet rs = null;

		rs = s.executeQuery(prop.getProperty("lp_act_select"));

		LinkedHashSet<String> code = new LinkedHashSet<String>();
		String cd = null;

		while (rs.next()) {
			cd = rs.getString("cd");
			code.add(cd);
			System.out.println(code);
		}

		s.executeQuery(prop.getProperty("lp_act_update"));
		conn.commit();

		return code;

	}

	private String createdtime(String access_token, String CNO, String createdtime) throws InterruptedException {
		// TODO Auto-generated method stub
		int count = 0;
		int maxtries = 5;
		String createdtimestring = null;
		String dy_edpt = prop.getProperty("lp_dy_edpt_ac");
		String dy_edpt_re = dy_edpt.replaceAll("\\{code\\}", CNO);
		String dy_created_modified = null;

		exit: while (true) {
			Response res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
					"Accept", ContentType.JSON).when().get(dy_edpt_re).then().extract().response();

			String response = res.asString();

			int Status_code = res.getStatusCode();

			if (Status_code != 200) {
				++count;

				if (count == 1) {
					Thread.sleep(10000L);
				}

				if (count == 2) {
					Thread.sleep(20000L);
				}
				if (count == 3) {
					Thread.sleep(30000L);
				}
				if (count == 4) {
					Thread.sleep(40000L);
				}

				if (count == maxtries) {
					childTest.log(Status.ERROR, String.valueOf(Status_code));
					childTest.log(Status.ERROR, CNO);
					createdtimestring = createdtime;
					break exit;
				}
			} else {
				JsonPath js = new JsonPath(response);
				createdtimestring = js.get("modifiedon");
				break exit;
			}
		}

		System.out.println(createdtimestring);

		return createdtimestring;
	}

	private long responsetimedifference(String TIMEST, String perftime) throws ParseException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(perftime));
		Date d = calendar.getTime();
		System.out.println(d);

		calendar.setTime(sdf.parse(TIMEST));
		Date dy = calendar.getTime();
		System.out.println(dy);

		long diff = d.getTime() - dy.getTime() - 25200000;

		long diffinsecs = diff / 1000;

		return diffinsecs;
	}

	private String Perftime(String createdtime) throws ParseException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(createdtime));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		return datetime;
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
