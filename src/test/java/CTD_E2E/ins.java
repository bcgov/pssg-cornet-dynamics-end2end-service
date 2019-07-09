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
import CTD_ContractTests.inscscliefn;

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

public class ins extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public String Filechart = System.getProperty("user.dir") + "/Chart/chart.xlsx";
	public delete_events deleteevent = new delete_events();
	public inscscliefn insc = new inscscliefn();
	public XSSFWorkbook workbook;
	public XSSFSheet sheet;
	public Iterator<Row> rows;
	public int rownum = 0;
	public Row row;
	chart ch = new chart();
	
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

	@Test
	public void insert()
			throws ClassNotFoundException, SQLException, IOException, InterruptedException, ParseException {

		parentTest = extent.createTest("load");
		childTest = parentTest.createNode("perft");

		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		String CNO;
		String TYPE = "TOMBSTONE";
		String TIMEST = null;

		int i = 0;

		while (i <= 9) {
			insc.insert_into_csclie();
			Thread.sleep(1000L);
			i++;
		}

		i = 0;

		while (i <= 9) {

			rs = s.executeQuery(prop.getProperty("lp_insert_oneselect"));

			while (rs.next()) {
				id = rs.getBigDecimal("id");
				idstring = String.valueOf(id);
				CNO = rs.getString("cno");
				TIMEST = rs.getString("TIMEST");

				String access_token = GetAccessToken();

				String createdtime = createdtime(access_token, CNO);

				String Perftime = Perftime(createdtime);

				double val = responsetimedifference(TIMEST, Perftime);

				rownum = sheet.getLastRowNum();
				row = sheet.createRow(i);
				int n = i + 1;
				row.createCell(0).setCellValue(n);
				row.createCell(1).setCellValue(val);
				i++;
			}
			FileOutputStream os = new FileOutputStream(Filechart);
			workbook.write(os);
			Application.launch(chart.class);
			
			/*for(i=0;i<=sheet.getLastRowNum(); i++)
			{
				row=sheet.getRow(i);
				sheet.removeRow(row);
			}*/
			
			File fd = new File(Filepath);
			fd.delete();
			

		}

	}

	private long responsetimedifference(String TIMEST, String perftime) throws ParseException {
		// TODO Auto-generated method stub

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(perftime));
		Date d = calendar.getTime();

		calendar.setTime(sdf.parse(TIMEST));
		Date dy = calendar.getTime();

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

	private String createdtime(String access_token, String CNO) throws InterruptedException {
		// TODO Auto-generated method stub


		int count = 0;
		int maxtries = 5;
		String createdtimestring = null;
		String dy_edpt=prop.getProperty("lp_insert_dy_edpt");
		String dy_edpt_re=dy_edpt.replaceAll("\\{CNO\\}", CNO);

		exit: while (true) {
			Response res = given()
					.headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON, "Accept",
							ContentType.JSON)
					.when()
					.get(dy_edpt_re)
					.then().extract().response();

			String response = res.asString();

			int Status_code = res.getStatusCode();

			if (Status_code != 200) {
				++count;

				if (count == 1) {
					Thread.sleep(20000L);
				}

				if (count == 2) {
					Thread.sleep(10000L);
				}
				if (count == 3) {
					Thread.sleep(30000L);
				}
				if (count == 4) {
					Thread.sleep(100000L);
				}

				if (count == maxtries) {
					childTest.log(Status.ERROR, String.valueOf(Status_code));
					Assert.assertTrue(false);
				}
			} else {
				JsonPath js = new JsonPath(response);
				createdtimestring = js.get("createdon");
				break exit;
			}

		}

		return createdtimestring;
	}

	public String GetAccessToken()
			throws MalformedURLException, IOException {
		
		final String CLIENT_ID = prop.getProperty("CLIENT_ID");
		final String CLIENT_SECRET =  prop.getProperty("CLIENT_SECRET");
		final String RESOURCE =  prop.getProperty("RESOURCE");
		final String AUTHORITY =  prop.getProperty("AUTHORITY");

		
		String parameters = "resource="
				+ URLEncoder.encode(RESOURCE, java.nio.charset.StandardCharsets.UTF_8.toString()) + "&client_id="
				+ URLEncoder.encode(CLIENT_ID, java.nio.charset.StandardCharsets.UTF_8.toString()) + "&client_secret="
				+ URLEncoder.encode(CLIENT_SECRET, java.nio.charset.StandardCharsets.UTF_8.toString())
				+ "&grant_type=client_credentials";

		URL url;
		HttpURLConnection connection = null;
		url = new URL(AUTHORITY);
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setDoOutput(true);
		connection.connect();

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		out.write(parameters);
		out.close();

		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
		}
		rd.close();

		Object jResponse;
		jResponse = JSONValue.parse(response.toString());
		JSONObject jObject = (JSONObject) jResponse;
		String access_token = jObject.get("access_token").toString();
		//System.out.println(access_token);

		return access_token;
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
