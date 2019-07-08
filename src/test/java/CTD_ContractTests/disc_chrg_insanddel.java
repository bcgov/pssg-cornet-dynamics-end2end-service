package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import CTD_ContractTests.delete_events;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class disc_chrg_insanddel extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	
	
	public FileInputStream fis;
	public Properties prop=new Properties();
	public String Filepath= System.getProperty("user.dir")+ "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	inscscliefn insc=new inscscliefn();
	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	Markup m;

	@BeforeClass
	public void jdbc() throws ClassNotFoundException, IOException {
		try {
			fis=new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
			deleteevent.delete();
		} catch (SQLException e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test
	public void insertanddeletedischarge()
			throws SQLException, ClassNotFoundException, IOException, ParseException {
		parentTest = extent.createTest(prop.getProperty("insertanddeletedischarge_parentextenttest"));
		parentTest.assignCategory(prop.getProperty("discategory"));
		
		Statement s = conn.createStatement();
		ResultSet rs = null;
		BigDecimal id = null;
		String idstring = null;
		BigDecimal clid = null;
		String CNO;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		insc.insert_into_csclie();

		rs = s.executeQuery(prop.getProperty("insertanddeletedischarge_oneselectquery"));

		if (rs.next()) {
			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			CNO = rs.getString("cno");
		} else {
			throw new SkipException("Retriving the new client created returned empty result set");
		}

		LinkedHashSet<String> disc_charge = insert_into_csdischarges(idstring, CNO, TYPE);
		delete(disc_charge, CNO, TYPE);

	}

	private void delete(LinkedHashSet<String> disc_charge, String CNO, String TYPE)
			throws ParseException, IOException, SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement t = conn.createStatement();
		String TIMEST = null;
		String idstring = null;

		try {

			childTest = parentTest.createNode("Delete");
			Iterator<String> it = disc_charge.iterator();
			idstring = it.next();
			TIMEST = it.next();

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			t.executeQuery(prop.getProperty("delete_onedeletequery")+""+idstring+"");
			conn.commit();			
			
			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, "90", "NULL");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get or = new or_get();

			String datetime = or_get.disc_chg_or_get(CNO, "90", gd);
			
			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_disc_chg();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, "90", datetime);

			deleteevent.delete();

		} catch (SQLException e) {
			throw new SkipException("Trouble with SQL triggers");
		} finally {
			t.close();

		}
	}

	private LinkedHashSet<String> insert_into_csdischarges(String clid, String CNO, String TYPE)
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		// TODO Auto-generated method stub

		Statement t = conn.createStatement();
		ResultSet rsinsert = null;
		BigDecimal id = null;
		String idstring = null;

		try {

			childTest = parentTest.createNode("Insert");

			String TIMEST = null;

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			String insert_into_csdischarges_oneinsert=prop.getProperty("insert_into_csdischarges_oneinsert");
			String query=insert_into_csdischarges_oneinsert.replaceAll("\\{clid\\}", clid);
			t.executeQuery(query);
			conn.commit();

			LinkedHashSet<String> discharge = new LinkedHashSet<String>();

			rsinsert = t.executeQuery(prop.getProperty("insert_into_csdischarges_oneselect")+""+clid + "");

			if (rsinsert.next()) {
				TIMEST = rsinsert.getString("TIMEST");
				id = rsinsert.getBigDecimal("id");
				idstring = String.valueOf(id);

				String[][] changedrequest = {
						{ prop.getProperty("CNO"), prop.getProperty("clntid"), prop.getProperty("rd"), prop.getProperty("chrgdt"), prop.getProperty("dischrg_id"), prop.getProperty("upddt") },
						{ CNO, clid, "90", "10-APR-2017", idstring, TIMEST } };
				m = MarkupHelper.createTable(changedrequest);
				childTest.log(Status.INFO, m);

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
				

				String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, "90", "NULL");

				childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
				n.event_notification();

				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
				or_get or = new or_get();

				String datetime = or_get.disc_chg_or_get(CNO, "90", gd);
				
				childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
				da.da_disc_chg();

				childTest.log(Status.INFO,
						MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
				unprocessed.exampleGetTest(TIMEST, TYPE, CNO, "90", datetime);

				deleteevent.delete();

				discharge.add(idstring);
				discharge.add(TIMEST);

			}

			return discharge;
		} catch (SQLException e) {
			throw new SkipException("Trouble with SQL triggers");
		} finally {
			t.close();
			rsinsert.close();
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
