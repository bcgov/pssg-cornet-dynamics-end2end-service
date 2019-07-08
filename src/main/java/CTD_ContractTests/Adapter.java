<<<<<<< master
package CTD_ContractTests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.Response;
import io.restassured.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.Is.is;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;

public class Adapter extends extentreport {

	String Filepath = System.getProperty("user.dir");
	String File;
	String bodypayload;
	String response = "NA";

	public FileInputStream fis;
	public FileInputStream fel;
	public Properties prop = new Properties();
	public Properties prol = new Properties();
	public String Fileprop = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public String Filepropmap = System.getProperty("user.dir") + "/Properties/rc.properties";
	
	public String generateStringFromResource(String path) throws IOException {

		return new String(Files.readAllBytes(Paths.get(path)));

	}

	public int stringint(String flgs) {
		int c = 0;

		if (flgs.equalsIgnoreCase("Y")) {
			c = Integer.valueOf(prop.getProperty("yflg"));
		} else if (flgs.equalsIgnoreCase("N")) {
			c = Integer.valueOf(prop.getProperty("nflg"));
		} else {
			Assert.assertTrue(false, "value not Y or N");
		}
		return c;
	}

	public Boolean tstone_flg(String flgs) {
		Boolean c = false;

		if (flgs.equalsIgnoreCase("Y")) {
			c = true;
		} else if (flgs.equalsIgnoreCase("N")) {
			c = false;
		} else {
			c = null;
		}
		return c;
	}

	public int rc(String r_c) throws IOException {
		
		fel = new FileInputStream(Filepropmap);
		prol.load(fel);
		
		int c = 0;
		

		String rc = prol.getProperty(r_c, "0");
		if (!rc.equals("0")) {
			c = Integer.valueOf(rc);
		}
		else
		{
			Assert.assertTrue(false, prol.getProperty("rc_list"));
		}

		return c;
	}

	public void transferreason() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/transferreason_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);

		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().body(bodypayload).header("Content-Type", "Application/json").when()
				.post(prop.getProperty("Transrea_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(code)).extract().response();

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if ((dynamicsadapter.get("desc")) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_cd")), desc);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_cd"))) {
				Assert.assertTrue(false,
						"The key " + prop.getProperty("disc_chg_reg_cd") + " is expected to be not present");
			}
		}

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		childTest.log(Status.INFO, "Response tested");
		response = res.asString();
	}

	public void da_disc_chg_reg() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/disciplinary_charge_regulation_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		int id = (int) map.get("id");
		String iid = Integer.toString(id);

		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("disc_chg_reg_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(iid)).extract().response();

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if (code != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_cd")), code);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_cd"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("disc_chg_reg_cd")
						+ " is expected to be not present in the response");
			}
		}

		if (desc != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_dsc")), desc);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_dsc"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("disc_chg_reg_dsc")
						+ " is expected to be not present in the response");
			}
		}

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		childTest.log(Status.PASS, MarkupHelper.createLabel("Response tested", colour.BLUE));

	}

	public void da_disc_chg() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/disciplinary_charge_ords.txt";
		bodypayload = generateStringFromResource(File);

		ArrayList<Map<String, ?>> request = JsonPath.with(bodypayload)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
				.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String id = (String) map.get("id");

		boolean b = true;
		String chg_id = null;

		if (b == (map.get(prop.getProperty("chg_id"))) instanceof Integer) {
			int chg_idi = (int) map.get(prop.getProperty("chg_id"));
			chg_id = Integer.toString(chg_idi);

		} else {
			BigDecimal chg_idi = (BigDecimal) map.get(prop.getProperty("chg_id"));
			chg_id = String.valueOf(chg_idi);
		}

		String cl_no = (String) map.get(prop.getProperty("cl_no"));
		int record_count = (int) map.get("record_count");

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("disc_charg_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(id)).body(prop.getProperty("dy_py_count"), is(record_count))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		Map<String, ?> dynamics_payload = res.path(prop.getProperty("dy_py"));

		String clb = (String) dynamics_payload.get(prop.getProperty("dischg_da_cldb"));
		String ichgtypeid = (String) dynamics_payload.get(prop.getProperty("dischg_icdb"));
		String rcltbind = prop.getProperty("dischgreq_clb") + "'" + cl_no + "')";
		String requestichgtypeid = prop.getProperty("dischg_custitransftypes") + "'" + chg_id + "')";
		Assert.assertEquals(clb, rcltbind, prop.getProperty("clb"));
		Assert.assertEquals(ichgtypeid, requestichgtypeid, prop.getProperty("ichgtypevald"));

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("or_extent_pass"), colour.GREEN));

	}

	public void da_trans() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		childTest = extent.createTest(prop.getProperty("trans_da_parent_extent"));

		File = Filepath + "/Dynamic_Adapter_RequestJSON/transfer_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String id = (String) map.get("id");
		String cl_no = (String) map.get(prop.getProperty("cl_no"));

		String trans_rea_code = (String) map.get(prop.getProperty("trans_rea_code"));
		int re_count = (int) map.get(prop.getProperty("re_count"));
		
		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when().post(prop.getProperty("trans_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_count"), is(re_count)).body(prop.getProperty("dy_py_id"), is(id))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, MarkupHelper
				.createLabel("Approximate response time in milliseconds: " + responsetime + "", colour.BLUE));

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		String clbind = (String) dynamicsadapter.get(prop.getProperty("dischg_da_cldb"));
		String transtypeid = (String) dynamicsadapter.get(prop.getProperty("trans_da_transtype"));
		String requestclbind = prop.getProperty("requestclbind") + "'" + cl_no + "')";
		String requesttranstypeid = prop.getProperty("requesttranstypeid") + "'" + trans_rea_code + "')";
		Assert.assertEquals(clbind, requestclbind, prop.getProperty("clibind_validation"));
		Assert.assertEquals(transtypeid, requesttranstypeid, prop.getProperty("transtype_validation"));

		// extent log
		childTest.log(Status.INFO, MarkupHelper.createLabel("Response tested", colour.BLUE));

	}

	public void da_tstonea(String gender) throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/tombstone_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String cl_no = (String) map.get(prop.getProperty("cl_no"));

		String clcd = (String) map.get(prop.getProperty("cla"));
		String gdcd = (String) map.get(prop.getProperty("gdcd"));
		String ln = (String) map.get(prop.getProperty("ln"));
		String fn = (String) map.get(prop.getProperty("fn"));
		String bd = (String) map.get(prop.getProperty("bd"));
		String rc = (String) map.get(prop.getProperty("rc"));

		if (rc != null) {
			int irc = rc(rc);
			rc = String.valueOf(irc);
		}

		String ind = (String) map.get(prop.getProperty("ind"));
		Boolean iind = null;

		if (ind == null) {
			iind = null;
		} else {
			iind = tstone_flg(ind);
		}

		Boolean ifna = null;

		String fna = (String) map.get(prop.getProperty("fna"));

		if (fna == null) {
			ifna = null;
		} else {
			ifna = tstone_flg(fna);
		}

		Boolean iinu = null;
		String inu = (String) map.get(prop.getProperty("inu"));

		if (inu == null) {
			iinu = null;
		} else {
			iinu = tstone_flg(inu);
		}

		Boolean imet = null;
		String met = (String) map.get(prop.getProperty("met"));

		if (met == null) {
			imet = null;
		} else {
			imet = tstone_flg(met);
		}

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("tdaedpt")).then().statusCode(200).body(prop.getProperty("dypy_fn"), is(fn))
				.body(prop.getProperty("dypy_ln"), is(ln)).body(prop.getProperty("dypy_clno"), is(cl_no))
				.body(prop.getProperty("dypy_bd"), is(bd)).body(prop.getProperty("dypy_gdcd"), is(gender))
				.body(prop.getProperty("dypy_fna"), is(ifna)).body(prop.getProperty("dypy_ind"), is(iind))
				.body(prop.getProperty("dypy_inu"), is(iinu)).body(prop.getProperty("dypy_met"), is(imet)).extract()
				.response();

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		JsonPath httpstatus = new JsonPath(response);
		int statuscode = httpstatus.getInt("http_status");

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		String request_rc = null;

		if (dynamicsadapter.get(prop.getProperty("src")) instanceof Integer) {
			int ir_c = (int) dynamicsadapter.get(prop.getProperty("src"));
			request_rc = String.valueOf(ir_c);
		}

		Assert.assertEquals(request_rc, rc);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adrt_ta"), colour.BLUE));

	}

	public void da_at() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/act_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("act_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("act_da_aid"), is(code)).body(prop.getProperty("act_da_desc"), is(desc))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

	}

	public void da_loc() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/location_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);

		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String idstring = null;

		if ((map.get("id")) instanceof Integer) {
			int id = (int) map.get("id");
			idstring = String.valueOf(id);
		} else {
			BigDecimal id = (BigDecimal) map.get("id");
			idstring = String.valueOf(id);
		}

		String desc = (String) map.get("desc");

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("loc_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_slid"), is(idstring)).extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		Assert.assertEquals(dynamicsadapter.get(prop.getProperty("dy_py_loc_ssg_name")), desc);

		if ((map.get("parent_id")) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("dy_py_rpc_db")),
					prop.getProperty("dy_pc_rpc_db_val") + "'" + (int) (map.get("parent_id")) + "')");
		} else {

			if (dynamicsadapter.containsKey(prop.getProperty("dy_py_rpc_db"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("dy_py_rpc_db")
						+ " is expected to be not present in the response");
			}
		}

	}

	public void da_stat() throws IOException {
		
		fis = new FileInputStream(Fileprop);
		prop.load(fis);
		
		File = Filepath + "/Dynamic_Adapter_RequestJSON/statute_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String idstring = null;

		if ((map.get("id")) instanceof Integer) {
			int id = (int) map.get("id");
			idstring = String.valueOf(id);
		} else {
			BigDecimal id = (BigDecimal) map.get("id");
			idstring = String.valueOf(id);
		}

		String rdescr = (String) map.get(prop.getProperty("stat_or_at_descr"));
		String rsc_no = (String) map.get(prop.getProperty("stat_or_sc_no"));
		String rpara_no = (String) map.get(prop.getProperty("stat_or_para_no"));
		String rsub_sc_no = (String) map.get(prop.getProperty("stat_or_sub_sec_no"));
		String rsub_para_no = (String) map.get(prop.getProperty("stat_or_sub_para_no"));
		String reff_dt = (String) map.get(prop.getProperty("stat_or_effe_dt"));
		String rexp_dt = (String) map.get(prop.getProperty("stat_or_expi_dt"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("stat_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_sta_id"), is(idstring))
				.body(prop.getProperty("dy_py_sta_sc_id"), is(rsc_no))
				.body(prop.getProperty("dy_py_sta_su_sc"), is(rsub_sc_no))
				.body(prop.getProperty("dy_py_sta_para"), is(rpara_no))
				.body(prop.getProperty("dy_py_sta_descr"), is(rdescr))
				.body(prop.getProperty("dy_py_sta_su_para"), is(rsub_para_no))
				.body(prop.getProperty("dy_py_sta_eff_dt"), is(reff_dt))
				.body(prop.getProperty("dy_py_sta_exp_dt"), is(rexp_dt)).extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if ((map.get(prop.getProperty("stat_or_at_code"))) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("rltdat")), prop.getProperty("rltdat_val") + "'"
					+ (String) (map.get(prop.getProperty("stat_or_at_code"))) + "')");
		} else {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("rltdat")),
					prop.getProperty("rltdat_val") + "'')");
		}

	}

}
=======
package CTD_ContractTests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.jayway.jsonpath.internal.JsonFormatter;

import CTD_Extent_Report.extentreport;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.config.JsonPathConfig.NumberReturnType;
import io.restassured.response.Response;
import io.restassured.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.core.Is.is;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;

public class Adapter extends extentreport {

	String Filepath = System.getProperty("user.dir");
	String File;
	String bodypayload;
	String response = "NA";

	public FileInputStream fis;
	public FileInputStream fel;
	public Properties prop = new Properties();
	public Properties prol = new Properties();
	public String Fileprop = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public String Filepropmap = System.getProperty("user.dir") + "/Properties/rc.properties";
	
	public String generateStringFromResource(String path) throws IOException {

		return new String(Files.readAllBytes(Paths.get(path)));

	}

	public int stringint(String flgs) {
		int c = 0;

		if (flgs.equalsIgnoreCase("Y")) {
			c = Integer.valueOf(prop.getProperty("yflg"));
		} else if (flgs.equalsIgnoreCase("N")) {
			c = Integer.valueOf(prop.getProperty("nflg"));
		} else {
			Assert.assertTrue(false, "value not Y or N");
		}
		return c;
	}

	public Boolean tstone_flg(String flgs) {
		Boolean c = false;

		if (flgs.equalsIgnoreCase("Y")) {
			c = true;
		} else if (flgs.equalsIgnoreCase("N")) {
			c = false;
		} else {
			c = null;
		}
		return c;
	}

	public int rc(String r_c) throws IOException {
		
		fel = new FileInputStream(Filepropmap);
		prol.load(fel);
		
		int c = 0;
		

		String rc = prol.getProperty(r_c, "0");
		if (!rc.equals("0")) {
			c = Integer.valueOf(rc);
		}
		else
		{
			Assert.assertTrue(false, prol.getProperty("rc_list"));
		}

		return c;
	}

	public void transferreason() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/transferreason_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);

		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().body(bodypayload).header("Content-Type", "Application/json").when()
				.post(prop.getProperty("Transrea_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(code)).extract().response();

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if ((dynamicsadapter.get("desc")) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_cd")), desc);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_cd"))) {
				Assert.assertTrue(false,
						"The key " + prop.getProperty("disc_chg_reg_cd") + " is expected to be not present");
			}
		}

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		childTest.log(Status.INFO, "Response tested");
		response = res.asString();
	}

	public void da_disc_chg_reg() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/disciplinary_charge_regulation_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		int id = (int) map.get("id");
		String iid = Integer.toString(id);

		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("disc_chg_reg_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(iid)).extract().response();

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if (code != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_cd")), code);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_cd"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("disc_chg_reg_cd")
						+ " is expected to be not present in the response");
			}
		}

		if (desc != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("disc_chg_reg_dsc")), desc);
		} else {
			if (dynamicsadapter.containsKey(prop.getProperty("disc_chg_reg_dsc"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("disc_chg_reg_dsc")
						+ " is expected to be not present in the response");
			}
		}

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		childTest.log(Status.PASS, MarkupHelper.createLabel("Response tested", colour.BLUE));

	}

	public void da_disc_chg() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/disciplinary_charge_ords.txt";
		bodypayload = generateStringFromResource(File);

		ArrayList<Map<String, ?>> request = JsonPath.with(bodypayload)
				.using(new JsonPathConfig(JsonPathConfig.NumberReturnType.BIG_DECIMAL))
				.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String id = (String) map.get("id");

		boolean b = true;
		String chg_id = null;

		if (b == (map.get(prop.getProperty("chg_id"))) instanceof Integer) {
			int chg_idi = (int) map.get(prop.getProperty("chg_id"));
			chg_id = Integer.toString(chg_idi);

		} else {
			BigDecimal chg_idi = (BigDecimal) map.get(prop.getProperty("chg_id"));
			chg_id = String.valueOf(chg_idi);
		}

		String cl_no = (String) map.get(prop.getProperty("cl_no"));
		int record_count = (int) map.get("record_count");

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("disc_charg_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_id"), is(id)).body(prop.getProperty("dy_py_count"), is(record_count))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		Map<String, ?> dynamics_payload = res.path(prop.getProperty("dy_py"));

		String clb = (String) dynamics_payload.get(prop.getProperty("dischg_da_cldb"));
		String ichgtypeid = (String) dynamics_payload.get(prop.getProperty("dischg_icdb"));
		String rcltbind = prop.getProperty("dischgreq_clb") + "'" + cl_no + "')";
		String requestichgtypeid = prop.getProperty("dischg_custitransftypes") + "'" + chg_id + "')";
		Assert.assertEquals(clb, rcltbind, prop.getProperty("clb"));
		Assert.assertEquals(ichgtypeid, requestichgtypeid, prop.getProperty("ichgtypevald"));

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("or_extent_pass"), colour.GREEN));

	}

	public void da_trans() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		childTest = extent.createTest(prop.getProperty("trans_da_parent_extent"));

		File = Filepath + "/Dynamic_Adapter_RequestJSON/transfer_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String id = (String) map.get("id");
		String cl_no = (String) map.get(prop.getProperty("cl_no"));

		String trans_rea_code = (String) map.get(prop.getProperty("trans_rea_code"));
		int re_count = (int) map.get(prop.getProperty("re_count"));
		
		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when().post(prop.getProperty("trans_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_count"), is(re_count)).body(prop.getProperty("dy_py_id"), is(id))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, MarkupHelper
				.createLabel("Approximate response time in milliseconds: " + responsetime + "", colour.BLUE));

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		String clbind = (String) dynamicsadapter.get(prop.getProperty("dischg_da_cldb"));
		String transtypeid = (String) dynamicsadapter.get(prop.getProperty("trans_da_transtype"));
		String requestclbind = prop.getProperty("requestclbind") + "'" + cl_no + "')";
		String requesttranstypeid = prop.getProperty("requesttranstypeid") + "'" + trans_rea_code + "')";
		Assert.assertEquals(clbind, requestclbind, prop.getProperty("clibind_validation"));
		Assert.assertEquals(transtypeid, requesttranstypeid, prop.getProperty("transtype_validation"));

		// extent log
		childTest.log(Status.INFO, MarkupHelper.createLabel("Response tested", colour.BLUE));

	}

	public void da_tstonea(String gender) throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/tombstone_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String cl_no = (String) map.get(prop.getProperty("cl_no"));

		String clcd = (String) map.get(prop.getProperty("cla"));
		String gdcd = (String) map.get(prop.getProperty("gdcd"));
		String ln = (String) map.get(prop.getProperty("ln"));
		String fn = (String) map.get(prop.getProperty("fn"));
		String bd = (String) map.get(prop.getProperty("bd"));
		String rc = (String) map.get(prop.getProperty("rc"));

		if (rc != null) {
			int irc = rc(rc);
			rc = String.valueOf(irc);
		}

		String ind = (String) map.get(prop.getProperty("ind"));
		Boolean iind = null;

		if (ind == null) {
			iind = null;
		} else {
			iind = tstone_flg(ind);
		}

		Boolean ifna = null;

		String fna = (String) map.get(prop.getProperty("fna"));

		if (fna == null) {
			ifna = null;
		} else {
			ifna = tstone_flg(fna);
		}

		Boolean iinu = null;
		String inu = (String) map.get(prop.getProperty("inu"));

		if (inu == null) {
			iinu = null;
		} else {
			iinu = tstone_flg(inu);
		}

		Boolean imet = null;
		String met = (String) map.get(prop.getProperty("met"));

		if (met == null) {
			imet = null;
		} else {
			imet = tstone_flg(met);
		}

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("tdaedpt")).then().statusCode(200).body(prop.getProperty("dypy_fn"), is(fn))
				.body(prop.getProperty("dypy_ln"), is(ln)).body(prop.getProperty("dypy_clno"), is(cl_no))
				.body(prop.getProperty("dypy_bd"), is(bd)).body(prop.getProperty("dypy_gdcd"), is(gender))
				.body(prop.getProperty("dypy_fna"), is(ifna)).body(prop.getProperty("dypy_ind"), is(iind))
				.body(prop.getProperty("dypy_inu"), is(iinu)).body(prop.getProperty("dypy_met"), is(imet)).extract()
				.response();

		String responsetime = Long.toString(res.getTimeIn(TimeUnit.MILLISECONDS));
		childTest.log(Status.INFO, "Approximate response time in milliseconds: " + responsetime + "");

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		JsonPath httpstatus = new JsonPath(response);
		int statuscode = httpstatus.getInt("http_status");

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		String request_rc = null;

		if (dynamicsadapter.get(prop.getProperty("src")) instanceof Integer) {
			int ir_c = (int) dynamicsadapter.get(prop.getProperty("src"));
			request_rc = String.valueOf(ir_c);
		}

		Assert.assertEquals(request_rc, rc);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adrt_ta"), colour.BLUE));

	}

	public void da_at() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/act_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);
		String code = (String) map.get(prop.getProperty("json_cd"));
		String desc = (String) map.get(prop.getProperty("json_dsc"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("act_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("act_da_aid"), is(code)).body(prop.getProperty("act_da_desc"), is(desc))
				.extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

	}

	public void da_loc() throws IOException {

		fis = new FileInputStream(Fileprop);
		prop.load(fis);

		File = Filepath + "/Dynamic_Adapter_RequestJSON/location_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);

		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String idstring = null;

		if ((map.get("id")) instanceof Integer) {
			int id = (int) map.get("id");
			idstring = String.valueOf(id);
		} else {
			BigDecimal id = (BigDecimal) map.get("id");
			idstring = String.valueOf(id);
		}

		String desc = (String) map.get("desc");

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("loc_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_slid"), is(idstring)).extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		Assert.assertEquals(dynamicsadapter.get(prop.getProperty("dy_py_loc_ssg_name")), desc);

		if ((map.get("parent_id")) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("dy_py_rpc_db")),
					prop.getProperty("dy_pc_rpc_db_val") + "'" + (int) (map.get("parent_id")) + "')");
		} else {

			if (dynamicsadapter.containsKey(prop.getProperty("dy_py_rpc_db"))) {
				Assert.assertTrue(false, "The key " + prop.getProperty("dy_py_rpc_db")
						+ " is expected to be not present in the response");
			}
		}

	}

	public void da_stat() throws IOException {
		
		fis = new FileInputStream(Fileprop);
		prop.load(fis);
		
		File = Filepath + "/Dynamic_Adapter_RequestJSON/statute_ords.txt";
		bodypayload = generateStringFromResource(File);

		JsonPath js = new JsonPath(bodypayload);
		ArrayList<Map<String, ?>> request = js.get(prop.getProperty("or_root"));

		Map<String, ?> map = request.get(0);

		String idstring = null;

		if ((map.get("id")) instanceof Integer) {
			int id = (int) map.get("id");
			idstring = String.valueOf(id);
		} else {
			BigDecimal id = (BigDecimal) map.get("id");
			idstring = String.valueOf(id);
		}

		String rdescr = (String) map.get(prop.getProperty("stat_or_at_descr"));
		String rsc_no = (String) map.get(prop.getProperty("stat_or_sc_no"));
		String rpara_no = (String) map.get(prop.getProperty("stat_or_para_no"));
		String rsub_sc_no = (String) map.get(prop.getProperty("stat_or_sub_sec_no"));
		String rsub_para_no = (String) map.get(prop.getProperty("stat_or_sub_para_no"));
		String reff_dt = (String) map.get(prop.getProperty("stat_or_effe_dt"));
		String rexp_dt = (String) map.get(prop.getProperty("stat_or_expi_dt"));

		Response res = given().header("Content-Type", "Application/json").body(bodypayload).when()
				.post(prop.getProperty("stat_da_edpt")).then().statusCode(200)
				.body(prop.getProperty("dy_py_sta_id"), is(idstring))
				.body(prop.getProperty("dy_py_sta_sc_id"), is(rsc_no))
				.body(prop.getProperty("dy_py_sta_su_sc"), is(rsub_sc_no))
				.body(prop.getProperty("dy_py_sta_para"), is(rpara_no))
				.body(prop.getProperty("dy_py_sta_descr"), is(rdescr))
				.body(prop.getProperty("dy_py_sta_su_para"), is(rsub_para_no))
				.body(prop.getProperty("dy_py_sta_eff_dt"), is(reff_dt))
				.body(prop.getProperty("dy_py_sta_exp_dt"), is(rexp_dt)).extract().response();

		response = res.asString();
		String[][] requestandresponse = { { "Request", "Response" },
				{ "<pre>" + JsonFormatter.prettyPrint(bodypayload) + "</pre>",
						"<pre>" + JsonFormatter.prettyPrint(response) + "</pre>" } };
		Markup m = MarkupHelper.createTable(requestandresponse);
		childTest.log(Status.INFO, m);

		Map<String, ?> dynamicsadapter = res.path(prop.getProperty("dy_py"));

		if ((map.get(prop.getProperty("stat_or_at_code"))) != null) {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("rltdat")), prop.getProperty("rltdat_val") + "'"
					+ (String) (map.get(prop.getProperty("stat_or_at_code"))) + "')");
		} else {
			Assert.assertEquals(dynamicsadapter.get(prop.getProperty("rltdat")),
					prop.getProperty("rltdat_val") + "'')");
		}

	}

}
>>>>>>> Sync with Master
