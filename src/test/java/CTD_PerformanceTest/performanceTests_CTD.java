package CTD_PerformanceTest;

import static io.restassured.RestAssured.given;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TimeZone;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import CTD_ContractTests.delete_events;
import CTD_Dynamics.dynamics;
import CTD_ContractTests.inscscliefn;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import CTD_Extent_Report.extentreport;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import javafx.application.Application;

public class performanceTests_CTD extends extentreport {

	String username;
	String password;
	Connection conn;

	FileInputStream fis;
	FileInputStream fs;
	Properties prop = new Properties();
	String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	String Filechart = System.getProperty("user.dir") + "/Chart/chart.xlsx";
	delete_events deleteevent = new delete_events();
	inscscliefn insc = new inscscliefn();
	XSSFWorkbook workbook;
	XSSFSheet sheet;
	Iterator<Row> rows;
	int rownum = 0;
	Row row;
	chart ch = new chart();
	datetime_for_excel pc = new datetime_for_excel();
	dynamics dy = new dynamics();
	delete_events delete = new delete_events();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException, IOException {
		try {
			delete.delete();
			fis = new FileInputStream(Filepath);
			prop.load(fis);
			fs = new FileInputStream(Filechart);
			workbook = new XSSFWorkbook(fs);
			sheet = workbook.getSheet("chart");
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

		int nu = Integer.valueOf(prop.getProperty("nu"));

		int clia_ins = (nu * 40) / 100;
		int tstone_ins = (nu * 30) / 100;
		int tstone_up = (nu * 20) / 100;
		int trans_ins = (nu * 10) / 100;

		LinkedHashSet<LinkedHashSet<String>> precliains = tstoneinsprep(clia_ins);
		Iterator<LinkedHashSet<String>> itcliains = precliains.iterator();
		LinkedHashSet<String> cliainscno = itcliains.next();
		LinkedHashSet<String> cliainsid = itcliains.next();
		LinkedList<String> cliainsfname = new LinkedList<String>();
		LinkedList<String> cliainslname = new LinkedList<String>();

		LinkedHashSet<LinkedHashSet<String>> pretstoneins = tstoneinsprep(tstone_ins);
		Iterator<LinkedHashSet<String>> it = pretstoneins.iterator();
		LinkedHashSet<String> tstonecno = it.next();
		LinkedHashSet<String> tstoneid = it.next();
		LinkedList<String> tstonefname = new LinkedList<String>();
		LinkedList<String> tstonelname = new LinkedList<String>();

		LinkedHashSet<LinkedHashSet<String>> pretransins = tstoneinsprep(trans_ins);
		Iterator<LinkedHashSet<String>> ittransins = pretransins.iterator();
		LinkedHashSet<String> transinscno = ittransins.next();
		LinkedHashSet<String> transinsid = ittransins.next();
		LinkedList<String> transinsfname = new LinkedList<String>();
		LinkedList<String> transinslname = new LinkedList<String>();

		LinkedHashSet<LinkedHashSet<String>> pretstoneup = tstoneinsprep(tstone_up);
		Iterator<LinkedHashSet<String>> ittstoneup = pretstoneup.iterator();
		LinkedHashSet<String> tstoneupcno = ittstoneup.next();
		LinkedHashSet<String> tstoneupid = ittstoneup.next();
		LinkedList<String> tstoneupfname = new LinkedList<String>();
		LinkedList<String> tstoneuplname = new LinkedList<String>();

		tstonefname = genString(tstone_ins);
		tstonelname = genString(tstone_ins);

		tstoneupfname = genString(tstone_up);
		tstoneuplname = genString(tstone_up);
		exectstoneins(tstone_up, tstoneupid, tstoneupfname, tstoneuplname);

		transinsfname = genString(trans_ins);
		transinslname = genString(trans_ins);
		exectstoneins(trans_ins, transinsid, transinsfname, transinslname);

		cliainsfname = genString(clia_ins);
		cliainslname = genString(clia_ins);
		exectstoneins(clia_ins, cliainsid, cliainsfname, cliainslname);
		LinkedHashSet<String> cliansau_id = cliains(clia_ins, cliainsid);

		Thread.sleep(200000L);

		delete.delete();

		int count = 1;
		int maxtries = 4;

		exit: while (true) {
			try {

				exectstoneins(tstone_ins, tstoneid, tstonefname, tstonelname);
				break exit;
			} catch (SQLException e) {
				if (++count == maxtries)
					throw e;
			}
		}

		exectstoneup(tstone_up, tstoneupcno);

		execcliaup(clia_ins, cliainsid, cliansau_id);

		exectransins(trans_ins, transinsid);

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

		Thread.sleep(10000L);

		tstoneinsperf(tstone_ins, tstonecno, tstonelname, 0, "tstoneins", 1);

		tstoneinsperf(clia_ins, cliainscno, cliainslname, 1, "cliains", tstone_ins + 1);

		tstoneinsperf(tstone_up, tstoneupcno, tstoneuplname, 1, "tstoneup", tstone_ins + clia_ins + 1);

		tstoneinsperf(trans_ins, transinscno, transinslname, 0, "transins", tstone_ins + clia_ins + tstone_up + 1);

		/*
		 * String datetime = pc.gettimest(); Filechart = System.getProperty("user.dir")
		 * + "/Chart/chart_" + "" + datetime + "" + ".xlsx";
		 */

		FileOutputStream os = new FileOutputStream(Filechart);
		workbook.write(os);
		Application.launch(chart.class);
		os.close();
	}

	public void exectstoneins(int tstone_ins, LinkedHashSet<String> tstoneid, LinkedList<String> tstonefname,
			LinkedList<String> tstonelname) throws SQLException {
		// TODO Auto-generated method stub

		int i = 1;
		String id = null;
		String gn_nm = null;
		String surn_nm = null;
		Iterator<String> it = tstoneid.iterator();
		Iterator<String> surnm = tstonelname.iterator();
		Iterator<String> gn = tstonefname.iterator();

		Statement s = conn.createStatement();
		String query = null;

		while (i <= tstone_ins) {
			id = it.next();
			gn_nm = gn.next();
			surn_nm = surnm.next();

			query = prop.getProperty("tlinsertcl_insert_csclienames_onehalf") + " " + id + ", 'Y', " + "'" + gn_nm + "'"
					+ ", '" + surn_nm + "'" + ", '" + gn_nm + "'" + ", '" + surn_nm + "')";
			s.executeQuery(query);
			conn.commit();
			i++;
		}

	}

	public void tstoneinsperf(int tstone_ins, LinkedHashSet<String> tstonecno, LinkedList<String> tstonelname,
			int created_modified, String event, int userno)
			throws SQLException, ClientProtocolException, IOException, InterruptedException, ParseException {
		// TODO Auto-generated method stub
		ResultSet perftstoneins = null;
		Statement ptstoneins = conn.createStatement();
		// Iterator<String> it = tstoneup_cno.iterator();
		String TIMEST = null;
		String CNO = null;
		int i = 1;
		int count = 1;
		int maxtries = 6;
		int countdouble = 1;
		int maxtriesdouble = 2;
		Iterator<String> it = tstonecno.iterator();
		Iterator<String> surn_nm = tstonelname.iterator();
		String createdtime = null;
		double val;

		while (i <= tstone_ins) {

			CNO = it.next();

			exit: while (true) {

				try {
					perftstoneins = ptstoneins.executeQuery(prop.getProperty("tstoneup_cnoselect") + "'" + CNO + "'");
					perftstoneins.next();

					TIMEST = perftstoneins.getString("TIMEST");

					break exit;
				} catch (SQLException e) {
					Thread.sleep(20000L);
					if (++count == maxtries)
						throw e;
				}
			}

			String access_token = dy.whenPostJsonUsingHttpClient_thenCorrect();

			exitdouble: while (true) {
				switch (event) {
				case "tstoneins":
					createdtime = createdtime(access_token, CNO, created_modified, "lastname", surn_nm.next(),
							"tstoneins", 1);
					break;

				case "tstoneup":
					createdtime = createdtime(access_token, CNO, created_modified, "gendercode", "2", "tstoneup", 1);
					break;

				case "transins":
					createdtime = createdtime(access_token, CNO + "TRS", created_modified, prop.getProperty("perf_cno"),
							CNO, "tstoneins", 2);
					break;

				case "cliains":
					createdtime = createdtime(access_token, CNO, created_modified, prop.getProperty("so"),
							prop.getProperty("so_val"), "tstoneup", 3);
					break;
				}

				String Perftime = Perftime(createdtime);

				val = responsetimedifference(TIMEST, Perftime);

				if (val < 0) {
					if (++countdouble <= maxtriesdouble) {
						Thread.sleep(80000L);
					} else {
						childTest.log(Status.INFO, "waited for 80 seconds but event did not reach dynamics yet");
					}

				} else {
					break exitdouble;
				}
			}

			rownum = sheet.getLastRowNum();
			row = sheet.createRow(rownum + 1);
			int n = userno;
			row.createCell(0).setCellValue(n);
			row.createCell(1).setCellValue(val);
			i++;
			userno++;

		}

	}

	public LinkedHashSet<String> transinsprep(int trans_ins) throws SQLException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub

		int i = 1;
		ResultSet transinspreprs = null;
		Statement transinsprep = conn.createStatement();
		BigDecimal id = null;
		String idstring = null;
		LinkedHashSet<String> transinsprep_cno = new LinkedHashSet<String>();

		while (i <= trans_ins) {
			insc.insert_into_csclie();
			i++;
		}

		transinspreprs = transinsprep.executeQuery(
				prop.getProperty("lp_insert_oneselect").replaceAll("\\{fc\\}", String.valueOf(trans_ins)));

		while (transinspreprs.next()) {
			id = transinspreprs.getBigDecimal("id");
			idstring = String.valueOf(id);
			transinsprep_cno.add(idstring);

		}

		return transinsprep_cno;
	}

	public LinkedHashSet<String> cliains(int clia_ins, LinkedHashSet<String> cliains_cno)
			throws InterruptedException, SQLException {
		// TODO Auto-generated method stub

		Statement cliains_q = conn.createStatement();
		Iterator<String> it = cliains_cno.iterator();
		String idstring = null;
		String clie_id = null;
		BigDecimal id = null;
		ResultSet rs = null;
		LinkedHashSet<String> au_id = new LinkedHashSet<String>();

		int i = 1;
		while (i <= clia_ins) {

			clie_id = it.next();
			cliains_q.executeQuery(prop.getProperty("clia_auth_ins").replaceAll("\\{clie_id\\}", clie_id));
			conn.commit();
			rs = cliains_q.executeQuery(prop.getProperty("clia_select") + "" + clie_id + "");
			rs.next();
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			au_id.add(idstring);
			Thread.sleep(1000L);
			i++;
		}

		return au_id;

	}

	public void exectransins(int trans_ins, LinkedHashSet<String> transins_cno) throws SQLException {
		// TODO Auto-generated method stub

		int i = 1;
		Statement transins = conn.createStatement();
		String id = null;

		Iterator<String> it = transins_cno.iterator();

		while (i <= trans_ins) {

			id = it.next();

			transins.executeQuery(prop.getProperty("trans_insmovecs_move_oneinsert").replaceAll("\\{id\\}", id));
			conn.commit();
			transins.executeQuery(prop.getProperty("trans_insmovecs_move_twoinsert").replaceAll("\\{id\\}", id));
			conn.commit();

			i++;
		}

	}

	public void execcliaup(int clia_ins, LinkedHashSet<String> cliains_cno, LinkedHashSet<String> au_id)
			throws InterruptedException, SQLException {
		// TODO Auto-generated method stub

		Statement cliains_q = conn.createStatement();
		Iterator<String> it = cliains_cno.iterator();
		Iterator<String> cliaau_id = au_id.iterator();
		String idstring = null;
		String query = null;
		String a_id = null;

		int i = 1;
		while (i <= clia_ins) {

			idstring = it.next();
			a_id = cliaau_id.next();
			query = prop.getProperty("clia_chrg_ins").replaceAll("\\{clie_id\\}", idstring);
			query = query.replaceAll("\\{au_id\\}", a_id);
			cliains_q.executeQuery(query);
			conn.commit();
			i++;
		}

	}

	public LinkedHashSet<LinkedHashSet<String>> tstoneinsprep(int tstone_ins)
			throws ClassNotFoundException, SQLException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		int i = 1;
		ResultSet rs = null;
		Statement tstoneupprep = conn.createStatement();
		BigDecimal id = null;
		String idstring = null;
		String CNO = null;
		LinkedHashSet<LinkedHashSet<String>> pretstoneins = new LinkedHashSet<LinkedHashSet<String>>();
		LinkedHashSet<String> tstoneins_cno = new LinkedHashSet<String>();
		LinkedHashSet<String> tstoneins_id = new LinkedHashSet<String>();
		// Iterator<LinkedHashSet<String>> it=pretstoneins.iterator();

		while (i <= tstone_ins) {
			insc.insert_into_csclie();
			i++;
		}

		String tstonepre = prop.getProperty("lp_insert_oneselect").replaceAll("\\{fc\\}", String.valueOf(tstone_ins));
		rs = tstoneupprep.executeQuery(tstonepre);

		while (rs.next()) {
			CNO = rs.getString("CNO");
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			tstoneins_cno.add(CNO);
			tstoneins_id.add(idstring);
		}

		pretstoneins.add(tstoneins_cno);
		pretstoneins.add(tstoneins_id);

		return pretstoneins;

	}

	public void exectstoneup(int tstone_up, LinkedHashSet<String> tstoneup_cno) throws SQLException {
		// TODO Auto-generated method stub
		Statement tstoneup = conn.createStatement();

		int i = 1;

		String itstoneupcno = null;

		Iterator<String> tstoneup_cno_it = tstoneup_cno.iterator();

		while (i <= tstone_up) {
			itstoneupcno = tstoneup_cno_it.next();
			tstoneup.executeQuery(prop.getProperty("tstoneup_gn_up") + "" + itstoneupcno + "");
			conn.commit();
			i++;
		}

	}

	public long responsetimedifference(String TIMEST, String perftime) throws ParseException {
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

	public String Perftime(String createdtime) throws ParseException {
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

	public String createdtime(String access_token, String CNO, int created_modified, String jspath, String dy,
			String events, int edpt) throws InterruptedException {
		// TODO Auto-generated method stub
		int count = 0;
		int maxtries = 5;
		String createdtimestring = null;
		String dy_edpt = null;
		String dy_edpt_re = null;
		String dy_created_modified = null;
		int gn = 0;

		switch (created_modified) {
		case 0:
			dy_created_modified = "modifiedon";
			break;
		case 1:
			dy_created_modified = "modifiedon";
			gn = Integer.valueOf(dy);
			break;
		}

		Response res = null;

		exit: while (true) {

			switch (edpt) {
			case 1:
				dy_edpt = prop.getProperty("lp_insert_dy_edpt");
				dy_edpt_re = dy_edpt.replaceAll("\\{CNO\\}", CNO);
				res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
						"Accept", ContentType.JSON).when().get(dy_edpt_re).then().extract().response();
				break;
			case 2:
				dy_edpt = prop.getProperty("lp_insert_dy_edpt_trans");
				dy_edpt_re = dy_edpt.replaceAll("\\{CNO\\}", CNO);
				res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
						"Accept", ContentType.JSON).when().get(dy_edpt_re).then().extract().response();
				break;
			case 3:
				dy_edpt = prop.getProperty("lp_insert_dy_edpt_cliains");
				dy_edpt_re = dy_edpt.replaceAll("\\{CNO\\}", CNO);
				res = given().headers("Authorization", "Bearer " + access_token, "Content-Type", ContentType.JSON,
						"Accept", ContentType.JSON).when().get(dy_edpt_re).then().extract().response();
				break;

			}

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
					Assert.assertTrue(false);
				}
			} else {
				JsonPath js = new JsonPath(response);

				switch (events) {
				case "tstoneins":
					if (js.get(jspath).equals(dy)) {
						createdtimestring = js.get(dy_created_modified);
						break exit;
					}
					break;
				case "tstoneup":
					if (js.get(jspath).equals(gn)) {
						createdtimestring = js.get(dy_created_modified);
						break exit;
					}
					break;
				}

			}

		}

		return createdtimestring;
	}

	public LinkedList<String> genString(int tstone_ins) {

		int i = 1;
		LinkedList<String> genString = new LinkedList<String>();

		while (i <= tstone_ins) {
			String generatedString = RandomStringUtils.randomAlphanumeric(20);
			genString.add(generatedString);
			i++;
		}

		return genString;
	}

	@AfterClass()
	public void jdbcclose() throws SQLException {
		conn.close();
	}

}
