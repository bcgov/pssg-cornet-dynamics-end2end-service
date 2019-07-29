package CTD_ContractTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;

public class or_get extends extentreport {

	static String username;
	static String password;
	static Connection connection;
	
	
	public static FileInputStream fis;
	public static Properties prop = new Properties();
	public static String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";

	public static String tstone_or(String clno, String gd, int clorcl_names)
			throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		try {
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			childTest.log(Status.INFO, MarkupHelper.createLabel("SQL Connection Refused", colour.BLUE));
		}
		
		

		ResultSet orrs = null;
		Statement or = connection.createStatement();
		String Status_request="SUCCESS";

			Response res = given().header("Content-Type", "Application/json").queryParam("client_no", clno).and()
					.queryParam("guid", gd).when()
					.get(prop.getProperty("tor_edpt")).then().assertThat()
					.statusCode(200).extract().response();

			String date = res.getHeader("Date");

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Date d = calendar.getTime();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getDefault());
			String datetime = formatter.format(d);

			String response = res.asString();
			String request = "none";

			String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

			String[][] requestandresponse = { { "Request", "Response" },
					{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
							"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
			Markup m = MarkupHelper.createTable(requestandresponse);

			ArrayList<Map<String, ?>> orget = res.path(prop.getProperty("or_root"));

			Map<String, ?> map = orget.get(0);

			String inu = (String) map.get(prop.getProperty("inu"));

			String clnoresponse = (String) map.get(prop.getProperty("cl_no"));

			String synresponse =(String) map.get(prop.getProperty("syn"));

			String claresponse = (String) map.get(prop.getProperty("cla"));

			String gdcdresponse = (String) map.get(prop.getProperty("gdcd"));

			String lnresponse = (String) map.get(prop.getProperty("ln"));

			String fnresponse = (String) map.get(prop.getProperty("fn"));

			String bdresponse = (String) map.get(prop.getProperty("bd"));

			String rc = (String) map.get(prop.getProperty("rc"));

			String ind = (String) map.get(prop.getProperty("ind"));

			String fna = (String) map.get(prop.getProperty("fna"));
			
			String met = (String) map.get(prop.getProperty("met"));
			
			String Status_response = (String) map.get("status");
			
			File file = new File(
					System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/tombstone_ords.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(request_dynamic_adapter);
			filewriter.close();

			
			if (clorcl_names == 1) {
				
				try {
			
				String r_c = null;
				String a_o = null;
				String f_a = null;
				String i_u = null;
				String m_t = null;


				orrs = or.executeQuery(prop.getProperty("tor_oneselect")+"'"+ clno + "'");
				orrs.next();
				BigDecimal id = orrs.getBigDecimal("id");
				String idstring=String.valueOf(id);
				String gdcd = orrs.getString("gdcd");
				String syn = orrs.getString("syn");
				String cla = orrs.getString("cla");
				
				orrs.close();

				orrs = or.executeQuery(
						prop.getProperty("tor_twoselect_onehalf")+""+ idstring + " "+prop.getProperty("tor_twoselect_twohalf"));
				orrs.next();
				String ln = orrs.getString("sn");
				String fn = orrs.getString("g1n");
				String bd = orrs.getString("bd");

				orrs = or.executeQuery(prop.getProperty("tor_threeselect")+"" + idstring + "");
				while (orrs.next()) {

					if (orrs.getString("pcd_cd").equals(prop.getProperty("r_c"))) {
						r_c = orrs.getString("pct_cd");
						
					}
					if (orrs.getString("pcd_cd").equals(prop.getProperty("a_o"))) {
						a_o = orrs.getString("pct_cd");
						
					}
					if (orrs.getString("pcd_cd").equals(prop.getProperty("f_a"))) {
						f_a = orrs.getString("pct_cd");
						
					}
					if (orrs.getString("pcd_cd").equals(prop.getProperty("i_u"))) {
						i_u = orrs.getString("pct_cd");
						
					}
					if (orrs.getString("pcd_cd").equals(prop.getProperty("m_t"))) {
						m_t = orrs.getString("pct_cd");
						
					}
				}

				Assert.assertEquals(clnoresponse, clno, prop.getProperty("vcor"));
				Assert.assertEquals(synresponse, syn, prop.getProperty("vsynor"));
				Assert.assertEquals(claresponse, cla, prop.getProperty("vclaor"));
				Assert.assertEquals(gdcdresponse, gdcd, prop.getProperty("vgdcdor"));
				Assert.assertEquals(lnresponse, ln, prop.getProperty("vlnor"));
				Assert.assertEquals(fnresponse, fn, prop.getProperty("vfnor"));
				Assert.assertEquals(bdresponse, bd, prop.getProperty("vbdor"));
				Assert.assertEquals(rc, r_c, prop.getProperty("vrcor"));
				Assert.assertEquals(ind, a_o, prop.getProperty("vabor"));
				Assert.assertEquals(fna, f_a, prop.getProperty("vfnaor"));
				Assert.assertEquals(met, m_t, prop.getProperty("vmtor"));
				Assert.assertEquals(inu, i_u,  prop.getProperty("vinuor"));
				Assert.assertEquals(Status_response, Status_request, prop.getProperty("vstor"));

				childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
				childTest.log(Status.INFO, m);			
			}finally {
				or.close();
				orrs.close();
				connection.close();
			}}
			else
			{
				Assert.assertEquals(clnoresponse, clno, prop.getProperty("vcor"));
				Assert.assertEquals(synresponse, null, prop.getProperty("vsynor"));
				Assert.assertEquals(claresponse, null,
						prop.getProperty("vclaor"));
				Assert.assertEquals(gdcdresponse, null, prop.getProperty("vgdcdor"));
				Assert.assertEquals(lnresponse, null, prop.getProperty("vlnor"));
				Assert.assertEquals(fnresponse, null, prop.getProperty("vfnor"));
				Assert.assertEquals(bdresponse, null, prop.getProperty("vbdor"));
				Assert.assertEquals(rc, null, prop.getProperty("vrcor"));
				Assert.assertEquals(ind, null, prop.getProperty("vabor"));
				Assert.assertEquals(fna, null, prop.getProperty("vfnaor"));
				Assert.assertEquals(met, null, prop.getProperty("vmtor"));
				Assert.assertEquals(inu, null,	prop.getProperty("vinuor"));
				Assert.assertEquals(Status_response, Status_request, prop.getProperty("vstor"));
				
				childTest.log(Status.PASS, MarkupHelper.createLabel(prop.getProperty("rort"), colour.GREEN));
				childTest.log(Status.INFO, m);	
			}
			
			return datetime;


	}

	public static String disc_chg_or_get(String cl_no, String rid, String gd)
			throws IOException, SQLException, ParseException {
		
		fis = new FileInputStream(Filepath);
		prop.load(fis);

		try {
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			childTest.log(Status.INFO, MarkupHelper.createLabel("SQL Connection Refused", colour.BLUE));
		}

		ResultSet orrs = null;
		Statement or = connection.createStatement();

		try {
			orrs = or.executeQuery(prop.getProperty("discharge_oneselect_onepart")+""+cl_no +" "+prop.getProperty("discharge_oneselect_twopart")+""+rid +" "+prop.getProperty("discharge_oneselect_threepart") );
			
			int i = 0;
			while (orrs.next()) {
				i++;
			}

			String stringi = Integer.toString(i);

			Response res = given().header("Content-Type", "Application/json").queryParam("client_no", cl_no).and()
					.queryParam("regu_id", rid).and().queryParam("guid", gd).when()
					.get(prop.getProperty("or_disc_chrg_edpt")).then().assertThat()
					.statusCode(200).extract().response();

			String date = res.getHeader("Date");

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Date d = calendar.getTime();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getDefault());
			String datetime = formatter.format(d);
			// System.out.println(datetime);

			String response = res.asString();
			String request = "none";
			String Status_request="SUCCESS";

			String[][] requestandresponse = { { "Request", "Response" },
					{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
							"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
			Markup m = MarkupHelper.createTable(requestandresponse);

			ArrayList<Map<String, ?>> ordsget = JsonPath.with(res.asString())
					.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

			Map<String, ?> map = ordsget.get(0);

			String id = (String) map.get("id");
			String cno = (String) map.get(prop.getProperty("cl_no"));
			String Status_response = (String) map.get("status");

			boolean b = true;
			String chg_id = null;

			if (b == (map.get(prop.getProperty("chg_id"))) instanceof Integer) {
				int chg_idi = (int) map.get(prop.getProperty("chg_id"));
				chg_id = Integer.toString(chg_idi);

			} else {
				BigDecimal chg_idi = (BigDecimal) map.get(prop.getProperty("chg_id"));
				chg_id = String.valueOf(chg_idi);
			}

			int record_counti = (int) map.get("record_count");
			String record_count = Integer.toString(record_counti);

			Assert.assertEquals(id, cl_no + rid, "Validating id");
			Assert.assertEquals(cno, cl_no);
			Assert.assertEquals(chg_id, rid);
			Assert.assertEquals(record_count, stringi);
			Assert.assertEquals(Status_response, Status_request, prop.getProperty("or_extent_pass"));

			childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
			childTest.log(Status.INFO, m);

			String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

			File file = new File(
					System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/disciplinary_charge_ords.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(request_dynamic_adapter);
			filewriter.close();

			return datetime;

		} finally {
			or.close();
			orrs.close();
			connection.close();
		}
	}

	// @Test
	public static String trans_or_get(String cl_no, String mry_cd, String gd)
			throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);

		try {
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			childTest.log(Status.INFO, MarkupHelper.createLabel("SQL Connection Refused", colour.BLUE));
		}

		ResultSet orrs = null;
		Statement or = connection.createStatement();

		//try {

			String query=prop.getProperty("trans_or_get_select");
			orrs = or.executeQuery(query.replaceAll("\\{cl_no\\}", cl_no));

			int i = 0;
			while (orrs.next()) {
				i++;
			}

			String stringi = Integer.toString(i);

			Response res = given().header("Content-Type", "Application/json").queryParam("client_no", cl_no).and()
					.queryParam("mrty_cd", mry_cd).and().queryParam("guid", gd).when()
					.get(prop.getProperty("trans_or_get_endpt")).then().assertThat()
					.statusCode(200).extract().response();

			String date = res.getHeader("Date");

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Date d = calendar.getTime();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getDefault());
			String datetime = formatter.format(d);

			String response = res.asString();
			String request = "none";
			String Status_request="SUCCESS";

			String[][] requestandresponse = { { "Request", "Response" },
					{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
							"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
			Markup m = MarkupHelper.createTable(requestandresponse);

			ArrayList<Map<String, ?>> ordsget = JsonPath.with(res.asString())
					.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

			Map<String, ?> map = ordsget.get(0);

			String id = (String) map.get("id");
			String cno = (String) map.get(prop.getProperty("cl_no"));
			String Status_response = (String) map.get("status");

			String trans_rea_code = (String) map.get(prop.getProperty("trans_rea_code"));

			int record_counti = (int) map.get("record_count");
			String record_count = Integer.toString(record_counti);

			Assert.assertEquals(id, cl_no + mry_cd, "Validating id");
			Assert.assertEquals(cno, cl_no);
			Assert.assertEquals(trans_rea_code, mry_cd);
			Assert.assertEquals(record_count, stringi);
			Assert.assertEquals(Status_response, Status_request, "Validating status");

			childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
			childTest.log(Status.INFO, m);

			String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

			File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/transfer_ords.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(request_dynamic_adapter);
			filewriter.close();

			return datetime;

		//} 
	/*finally {
			or.close();
			orrs.close();
			connection.close();
		}*/
	}

	public static String stat_or_get(String id, String gd) throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);

		try {
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
		} catch (Exception e) {
			childTest.log(Status.INFO, MarkupHelper.createLabel("SQL Connection Refused", colour.BLUE));
		}

		ResultSet orrs = null;
		Statement or = connection.createStatement();

		try {

			orrs = or.executeQuery(prop.getProperty("stat_or_oneselect")+""+ id + "");

			try {
				orrs.next();

			} catch (Exception e) {
				throw new SkipException("error in event trigger");
			}

			String lty_cd = orrs.getString("lty_cd");
			String descr = orrs.getString("descr");
			String sc_no = orrs.getString("sc_no");
			String para_no = orrs.getString("para_no");
			String sub_sc_no = orrs.getString("sub_sc_no");
			String sub_para_no = orrs.getString("sub_para_no");
			String eff_dt = orrs.getString("eff_dt");
			String exp_dt = orrs.getString("exp_dt");
			String status_request="SUCCESS";

			Response res = given().header("Content-Type", "Application/json").queryParam("stat_id", id).and()
					.queryParam("guid", gd).when().get(prop.getProperty("stat_or_edpt"))
					.then().assertThat().statusCode(200).extract().response();

			String date = res.getHeader("Date");

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(date));
			Date d = calendar.getTime();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formatter.setTimeZone(TimeZone.getDefault());
			String datetime = formatter.format(d);

			String response = res.asString();
			String request = "none";

			String[][] requestandresponse = { { "Request", "Response" },
					{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
							"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
			Markup m = MarkupHelper.createTable(requestandresponse);

			ArrayList<Map<String, ?>> orget = JsonPath.with(response)
					.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

			Map<String, ?> map = orget.get(0);

			boolean b = true;

			String responseid = null;

			if (b == (map.get("id") instanceof Integer)) {
				int responseidi = (int) map.get("id");
				responseid = String.valueOf(responseidi);
			} else {
				BigDecimal responseidi = (BigDecimal) map.get("id");
				responseid = String.valueOf(responseidi);
			}

			String at_code = (String) map.get(prop.getProperty("stat_or_at_code"));
			String rdescr = (String) map.get(prop.getProperty("stat_or_at_descr"));
			String rsc_no = (String) map.get(prop.getProperty("stat_or_sc_no"));
			String rpara_no = (String) map.get(prop.getProperty("stat_or_para_no"));
			String rsub_sc_no = (String) map.get(prop.getProperty("stat_or_sub_sec_no"));
			String rsub_para_no = (String) map.get(prop.getProperty("stat_or_sub_para_no"));
			String reff_dt = (String) map.get(prop.getProperty("stat_or_effe_dt"));
			String rexp_dt = (String) map.get(prop.getProperty("stat_or_expi_dt"));
			String Status_response = (String) map.get("status");

			Assert.assertEquals(responseid, id, "Validating id");
			Assert.assertEquals(at_code, lty_cd);
			Assert.assertEquals(rdescr, descr);
			Assert.assertEquals(rsc_no, sc_no);
			Assert.assertEquals(rpara_no, para_no);
			Assert.assertEquals(rsub_sc_no, sub_sc_no);
			Assert.assertEquals(rsub_para_no, sub_para_no);
			Assert.assertEquals(reff_dt, eff_dt);
			Assert.assertEquals(rexp_dt, exp_dt);
			Assert.assertEquals(Status_response, status_request, "Validating status");

			childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
			childTest.log(Status.INFO, m);

			String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

			File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/statute_ords.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(request_dynamic_adapter);
			filewriter.close();

			return datetime;

		} finally {
			or.close();
			orrs.close();
			connection.close();
		}
	}

	public static String stat_del_or_get(String id, String gd, String exp_dt, String eff_dt,
			String para_no, String sub_para_no, String sub_sc_no, String sc_no, String lty_cd,
			String pty_cd, String descr) throws IOException, SQLException, ParseException {
		
		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		Response res = given().header("Content-Type", "Application/json").queryParam("stat_id", id).and()
				.queryParam("guid", gd).when().get(prop.getProperty("stat_or_edpt"))
				.then().assertThat().statusCode(200).extract().response();

		String date = res.getHeader("Date");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		String response = res.asString();
		String request = "none";
		String status_request="SUCCESS";
		

		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);

		ArrayList<Map<String, ?>> ordsget = JsonPath.with(response)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

		Map<String, ?> map = ordsget.get(0);

		boolean b = true;

		String responseid = null;

		if (b == (map.get("id") instanceof Integer)) {
			int responseidi = (int) map.get("id");
			responseid = String.valueOf(responseidi);
		} else {
			BigDecimal responseidi = (BigDecimal) map.get("id");
			responseid = String.valueOf(responseidi);
		}

		String at_code = (String) map.get(prop.getProperty("stat_or_at_code"));
		String rdescr = (String) map.get(prop.getProperty("stat_or_at_descr"));
		String rsc_no = (String) map.get(prop.getProperty("stat_or_sc_no"));
		String rpara_no = (String) map.get(prop.getProperty("stat_or_para_no"));
		String rsub_sc_no = (String) map.get(prop.getProperty("stat_or_sub_sec_no"));
		String rsub_para_no = (String) map.get(prop.getProperty("stat_or_sub_para_no"));
		String reff_dt = (String) map.get(prop.getProperty("stat_or_eff_dt"));
		String rexp_dt = (String) map.get(prop.getProperty("stat_or_exp_dt"));
		String Status_response = (String) map.get("status");

		Assert.assertEquals(responseid, id, "Validating id");
		Assert.assertEquals(at_code, lty_cd);
		Assert.assertEquals(rdescr, descr);
		Assert.assertEquals(rsc_no, sc_no);
		Assert.assertEquals(rpara_no, para_no);
		Assert.assertEquals(rsub_sc_no, sub_sc_no);
		Assert.assertEquals(rsub_para_no, sub_para_no);
		Assert.assertEquals(reff_dt, eff_dt);
		Assert.assertEquals(rexp_dt, exp_dt);
		Assert.assertEquals(Status_response, status_request, "Validating status");

		childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
		childTest.log(Status.INFO, m);

		String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

		File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/statute_ords.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();

		return datetime;

	}

	public static String trans_rea_or_get(String mry_cd, String gd, String descr)
			throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		Response res = given().header("Content-Type", "Application/json").queryParam("mrty_cd", mry_cd).and()
				.queryParam("guid", gd).when()
				.get(prop.getProperty("Transrea_or_edpt")).then().assertThat()
				.statusCode(200).extract().response();

		String request = "none";
		String response = res.asString();
		String status_request="SUCCESS";

		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);

		ArrayList<Map<String, ?>> ordsget = JsonPath.with(response)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

		Map<String, ?> map = ordsget.get(0);
		String rcode = (String) map.get(prop.getProperty("json_cd"));
		String rdescr = (String) map.get(prop.getProperty("json_dsc"));
		String Status_response = (String) map.get("status");

		Assert.assertEquals(rcode, mry_cd);
		Assert.assertEquals(rdescr, descr);
		Assert.assertEquals(Status_response, status_request, "Validating status");

		String date = res.getHeader("Date");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

		File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/transferreason_ords.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();
		childTest.log(Status.PASS, MarkupHelper.createLabel("response tested", colour.GREEN));
		childTest.log(Status.INFO, m);

		return datetime;
	}

	public static String at_or_get(String a_cd, String gd, String descr)
			throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		Response res = given().header("Content-Type", "Application/json").queryParam("act_cd", a_cd).and()
				.queryParam("guid", gd).when().get(prop.getProperty("act_od_edpt")).then()
				.assertThat().statusCode(200).extract().response();

		String request = "none";
		String response = res.asString();
		String status_request="SUCCESS";

		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);

		ArrayList<Map<String, ?>> ordsget = JsonPath.with(response)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get(prop.getProperty("or_root"));

		Map<String, ?> map = ordsget.get(0);
		String code = (String) map.get(prop.getProperty("json_cd"));
		String Desc = (String) map.get(prop.getProperty("json_dsc"));
		String Status_response = (String) map.get(prop.getProperty("json_status"));

		Assert.assertEquals(code, a_cd, prop.getProperty("v_code_or") );
		Assert.assertEquals(Desc, descr, prop.getProperty("v_desc_or"));
		Assert.assertEquals(Status_response, status_request, prop.getProperty("v_st_or"));

		String date = res.getHeader("Date");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

		File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/act_ords.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();
		childTest.log(Status.PASS, MarkupHelper.createLabel(prop.getProperty("or_extent_pass"), colour.GREEN));
		childTest.log(Status.INFO, m);

		return datetime;
	}

	public static String disc_chg_reg_or_get(int rid, String code, String gd, String descr,
			String eff_dt, String exp_dt) throws IOException, SQLException, ParseException {

		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		Response res = given().header("Content-Type", "Application/json").queryParam("regu_id", rid).and()
				.queryParam("guid", gd).when().get(prop.getProperty("disc_chg_reg_or_edpt"))
				.then().assertThat().statusCode(200).extract().response();

		String request = "none";
		String response = res.asString();
		String status_request="SUCCESS";

		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);

		ArrayList<Map<String, ?>> ordsget = JsonPath.with(response)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get("cms_data");

		Map<String, ?> map = ordsget.get(0);
		String coder = (String) map.get(prop.getProperty("json_cd"));
		String rdesc = (String) map.get(prop.getProperty("json_dsc"));
		int id = (int) map.get("id");
		String reff_dt = (String) map.get(prop.getProperty("eff_dt"));
		String rexp_dt = (String) map.get(prop.getProperty("exp_dt"));
		String Status_response = (String) map.get("status");

		Assert.assertEquals(coder, code);
		Assert.assertEquals(rdesc, descr);
		Assert.assertEquals(id, rid);
		Assert.assertEquals(reff_dt, eff_dt);
		Assert.assertEquals(rexp_dt, exp_dt);
		Assert.assertEquals(Status_response, status_request, "Validating status");

		String date = res.getHeader("Date");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

		File file = new File(System.getProperty("user.dir")
				+ "/Dynamic_Adapter_RequestJSON/disciplinary_charge_regulation_ords.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();
		childTest.log(Status.PASS, MarkupHelper.createLabel("Response tested", colour.GREEN));
		childTest.log(Status.INFO, m);

		return datetime;
	}

	public String loc_or_get(String idstring, String code, String desc, String loc_id, String eff_dt,
			String exp_dt, String gd) throws IOException, ParseException {
		// TODO Auto-generated method stub

		fis = new FileInputStream(Filepath);
		prop.load(fis);
		
		Response res = given().header("Content-Type", "Application/json").queryParam("loca_id", idstring).and()
				.queryParam("guid", gd).when().get(prop.getProperty("loc_or_edpt"))
				.then().assertThat().statusCode(200).extract().response();

		String date = res.getHeader("Date");

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(date));
		Date d = calendar.getTime();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		formatter.setTimeZone(TimeZone.getDefault());
		String datetime = formatter.format(d);

		String request = "none";
		String response = res.asString();
		String status_request="SUCCESS";

		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(request) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);

		ArrayList<Map<String, ?>> ordsget = JsonPath.with(response)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL)).get("cms_data");

		Map<String, ?> map = ordsget.get(0);
		String coder = (String) map.get(prop.getProperty("json_cd"));
		String Desc = (String) map.get(prop.getProperty("json_dsc"));
		String Status_response = (String) map.get(prop.getProperty("json_status"));

		boolean b = true;

		String responseid = null;

		String par_id = null;

		if (loc_id != null) {
			if (b == (map.get(prop.get("l_p_id")) instanceof Integer)) {
				int par_idi = (int) map.get(prop.get("l_p_id"));
				par_id = String.valueOf(par_idi);
			} else {
				BigDecimal par_idi = (BigDecimal) map.get(prop.get("l_p_id"));
				par_id = String.valueOf(par_idi);
			}
		}

		if (b == (map.get("id") instanceof Integer)) {
			int responseidi = (int) map.get("id");
			responseid = String.valueOf(responseidi);
		} else {
			BigDecimal responseidi = (BigDecimal) map.get("id");
			responseid = String.valueOf(responseidi);
		}

		String reff_dt = (String) map.get(prop.getProperty("eff_dt"));
		String rexp_dt = (String) map.get(prop.getProperty("exp_dt"));

		Assert.assertEquals(coder, code);
		Assert.assertEquals(Desc, desc);
		Assert.assertEquals(responseid, idstring);
		Assert.assertEquals(reff_dt, eff_dt);
		Assert.assertEquals(rexp_dt, exp_dt);
		Assert.assertEquals(par_id, loc_id);
		Assert.assertEquals(Status_response, status_request, "Validating status");
		
		childTest.log(Status.PASS, MarkupHelper.createLabel("Response tested", colour.GREEN));
		childTest.log(Status.INFO, m);

		String request_dynamic_adapter = JsonFormatter.prettyPrint(response);

		File file = new File(System.getProperty("user.dir") + "/Dynamic_Adapter_RequestJSON/location_ords.txt");
		FileWriter filewriter = new FileWriter(file);
		filewriter.write(request_dynamic_adapter);
		filewriter.close();

		return datetime;
	}

}
