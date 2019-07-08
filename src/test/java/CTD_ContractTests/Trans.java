<<<<<<< HEAD
<<<<<<< master
=======
>>>>>>> Dev
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
import CTD_ContractTests.Trans_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class Trans extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	public Connection insertconn;
	public FileInputStream fis;
	public Properties prop=new Properties();
	public String Filepath= System.getProperty("user.dir")+ "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	Trans_Event_verify_event unprocessed = new Trans_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc=new inscscliefn();
	

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
		try {
			fis=new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
			deleteevent.delete();
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test()
	public void insert() throws SQLException, ClassNotFoundException, ParseException, IOException {

		ResultSet rs = null;
		Statement f = conn.createStatement();
		
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		try {
			parentTest = extent.createTest(prop.getProperty("trans_insert_parent_extent"));
			parentTest.assignCategory(prop.getProperty("trans_type"));
			
			System.out.println("..");
			
			insc.insert_into_csclie();

			rs = f.executeQuery(prop.getProperty("trans_insert_oneselect"));

			if (rs.next()) {
				id = rs.getBigDecimal("id");
				idstring = String.valueOf(id);
				CNO = rs.getString("cno");
			} else {
				throw new SkipException("Retriving the new client created returned empty result set");
			}

			LinkedHashSet<String> insmove = insmovecs_move(idstring, CNO);
			String upd_dt = updateac_dt_tm(insmove);
			Deleteinsdmove(insmove, upd_dt);

		} finally {
			rs.close();
		}

	}

	private void Deleteinsdmove(LinkedHashSet<String> insmove, String upd_dt) throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement t = conn.createStatement();

		childTest = parentTest.createNode(prop.getProperty("trans_delete_onechild"));
		
		String TYPE=prop.getProperty("trans_type");
		
		Iterator<String> it=insmove.iterator();
		
		String id=it.next();
		it.next();
		String CNO=it.next();
		
		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		t.executeQuery(prop.getProperty("trans_delete_onedel")+""+id+"");
		conn.commit();
		
		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(upd_dt, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);
		
		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_trans();		
		
		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
		unprocessed.exampleGetTest(upd_dt, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

		deleteevent.delete();
		
	}

	private String updateac_dt_tm(LinkedHashSet<String> insdmove)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement t = conn.createStatement();

		childTest = parentTest.createNode(prop.getProperty("trans_updateac_dt_tm_onechild"));

		String TYPE = prop.getProperty("trans_type");
		String TIMEST = null;

		Markup m;

		Iterator<String> it = insdmove.iterator();

		String id = it.next();
		String clid = it.next();
		String CNO = it.next();
		String mty_cd = it.next();
		String mry_cd = it.next();
		String ac_dt_tm = it.next();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		t.executeQuery(prop.getProperty("trans_updateac_dt_tm_oneupdate")+"" + id + "");
		conn.commit();

		ResultSet rs = t.executeQuery(
				prop.getProperty("trans_updateac_dt_tm_oneselect")+""+ id + "");

		if (rs.next()) {
			TIMEST = rs.getString("TIMEST");

			String[][] insmove = { { "ID", prop.getProperty("clntid"), prop.getProperty("CNO"), prop.getProperty("mty_cd"), prop.getProperty("mry_cd"), prop.getProperty("ac_dt_tm"), "ENT_DT" },
					{ id, clid, CNO, mty_cd, mry_cd, ac_dt_tm, TIMEST } };
			m = MarkupHelper.createTable(insmove);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel( prop.getProperty("db_get"), colour.BLUE));
			String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_trans();	
			
			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

			deleteevent.delete();
		}
		return TIMEST;

	}

	private LinkedHashSet<String> insmovecs_move(String id, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException { // TODO Auto-generated method stub

		Statement t = conn.createStatement();

		String TYPE = prop.getProperty("trans_type");
		BigDecimal cs_move_id = null;
		String TIMEST = null;
		String cs_move_idstring = null;
		String mty_cd = null;
		String mry_cd = null;
		String ac_dt_tm = null;
		Markup m;
		LinkedHashSet<String> insdmove = new LinkedHashSet<String>();

		childTest = parentTest.createNode(prop.getProperty("trans_insmovecs_move_onechild"));

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		
		String one_insert=prop.getProperty("trans_insmovecs_move_oneinsert");
		String two_insert=prop.getProperty("trans_insmovecs_move_twoinsert");
		String query=one_insert.replaceAll("\\{id\\}", id);
		
		t.executeQuery(query);
		conn.commit();

		query=two_insert.replaceAll("\\{id\\}", id);
		
		t.executeQuery(query);
		conn.commit();

		String oneselect=prop.getProperty("trans_insmovecs_move_oneselect");
		query=oneselect.replaceAll("\\{id\\}", id);
		ResultSet rs = t.executeQuery(query);

		if (rs.next()) {
			cs_move_id = rs.getBigDecimal("id");
			cs_move_idstring = String.valueOf(cs_move_id);
			TIMEST = rs.getString("TIMEST");
			mty_cd = rs.getString("MTY_CD");
			mry_cd = rs.getString("MRY_CD");
			ac_dt_tm = rs.getString("ac_dt_tm");

			String[][] insmove = { { "ID", prop.getProperty("clntid"), prop.getProperty("CNO"), prop.getProperty("mty_cd"), prop.getProperty("mry_cd"), prop.getProperty("ac_dt_tm"), "ENT_DT" },
					{ cs_move_idstring, id, CNO, mty_cd, mry_cd, ac_dt_tm, TIMEST } };
			m = MarkupHelper.createTable(insmove);
			childTest.log(Status.INFO, m);

			insdmove.add(cs_move_idstring);
			insdmove.add(id);
			insdmove.add(CNO);
			insdmove.add(mty_cd);
			insdmove.add(mry_cd);
			insdmove.add(ac_dt_tm);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: ORDS GET", colour.BLUE));
			String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_trans();	
			
			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

			deleteevent.delete();

		}

		return insdmove;
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
<<<<<<< HEAD
=======
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
import CTD_ContractTests.Trans_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class Trans extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	public Connection insertconn;
	public FileInputStream fis;
	public Properties prop=new Properties();
	public String Filepath= System.getProperty("user.dir")+ "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();
	
	Trans_Event_verify_event unprocessed = new Trans_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc=new inscscliefn();
	

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
		try {
			fis=new FileInputStream(Filepath);
			prop.load(fis);
			username = prop.getProperty("sql_username");
			password = prop.getProperty("sql_password");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(prop.getProperty("sql_connection"), username, password);
			deleteevent.delete();
		} catch (Exception e) {
			throw new SkipException("SQL connection refused and therefore all tests are skipped");
		}
	}

	@Test()
	public void insert() throws SQLException, ClassNotFoundException, ParseException, IOException {

		ResultSet rs = null;
		Statement f = conn.createStatement();
		
		BigDecimal id = null;
		String idstring = null;
		String CNO;

		try {
			parentTest = extent.createTest(prop.getProperty("trans_insert_parent_extent"));
			parentTest.assignCategory(prop.getProperty("trans_type"));
			
			System.out.println("..");
			
			insc.insert_into_csclie();

			rs = f.executeQuery(prop.getProperty("trans_insert_oneselect"));

			if (rs.next()) {
				id = rs.getBigDecimal("id");
				idstring = String.valueOf(id);
				CNO = rs.getString("cno");
			} else {
				throw new SkipException("Retriving the new client created returned empty result set");
			}

			LinkedHashSet<String> insmove = insmovecs_move(idstring, CNO);
			String upd_dt = updateac_dt_tm(insmove);
			Deleteinsdmove(insmove, upd_dt);

		} finally {
			rs.close();
		}

	}

	private void Deleteinsdmove(LinkedHashSet<String> insmove, String upd_dt) throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement t = conn.createStatement();

		childTest = parentTest.createNode(prop.getProperty("trans_delete_onechild"));
		
		String TYPE=prop.getProperty("trans_type");
		
		Iterator<String> it=insmove.iterator();
		
		String id=it.next();
		it.next();
		String CNO=it.next();
		
		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		t.executeQuery(prop.getProperty("trans_delete_onedel")+""+id+"");
		conn.commit();
		
		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(upd_dt, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);
		
		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_trans();		
		
		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
		unprocessed.exampleGetTest(upd_dt, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

		deleteevent.delete();
		
	}

	private String updateac_dt_tm(LinkedHashSet<String> insdmove)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Statement t = conn.createStatement();

		childTest = parentTest.createNode(prop.getProperty("trans_updateac_dt_tm_onechild"));

		String TYPE = prop.getProperty("trans_type");
		String TIMEST = null;

		Markup m;

		Iterator<String> it = insdmove.iterator();

		String id = it.next();
		String clid = it.next();
		String CNO = it.next();
		String mty_cd = it.next();
		String mry_cd = it.next();
		String ac_dt_tm = it.next();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		t.executeQuery(prop.getProperty("trans_updateac_dt_tm_oneupdate")+"" + id + "");
		conn.commit();

		ResultSet rs = t.executeQuery(
				prop.getProperty("trans_updateac_dt_tm_oneselect")+""+ id + "");

		if (rs.next()) {
			TIMEST = rs.getString("TIMEST");

			String[][] insmove = { { "ID", prop.getProperty("clntid"), prop.getProperty("CNO"), prop.getProperty("mty_cd"), prop.getProperty("mry_cd"), prop.getProperty("ac_dt_tm"), "ENT_DT" },
					{ id, clid, CNO, mty_cd, mry_cd, ac_dt_tm, TIMEST } };
			m = MarkupHelper.createTable(insmove);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel( prop.getProperty("db_get"), colour.BLUE));
			String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_trans();	
			
			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

			deleteevent.delete();
		}
		return TIMEST;

	}

	private LinkedHashSet<String> insmovecs_move(String id, String CNO)
			throws SQLException, ParseException, IOException, ClassNotFoundException { // TODO Auto-generated method stub

		Statement t = conn.createStatement();

		String TYPE = prop.getProperty("trans_type");
		BigDecimal cs_move_id = null;
		String TIMEST = null;
		String cs_move_idstring = null;
		String mty_cd = null;
		String mry_cd = null;
		String ac_dt_tm = null;
		Markup m;
		LinkedHashSet<String> insdmove = new LinkedHashSet<String>();

		childTest = parentTest.createNode(prop.getProperty("trans_insmovecs_move_onechild"));

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
		
		String one_insert=prop.getProperty("trans_insmovecs_move_oneinsert");
		String two_insert=prop.getProperty("trans_insmovecs_move_twoinsert");
		String query=one_insert.replaceAll("\\{id\\}", id);
		
		t.executeQuery(query);
		conn.commit();

		query=two_insert.replaceAll("\\{id\\}", id);
		
		t.executeQuery(query);
		conn.commit();

		String oneselect=prop.getProperty("trans_insmovecs_move_oneselect");
		query=oneselect.replaceAll("\\{id\\}", id);
		ResultSet rs = t.executeQuery(query);

		if (rs.next()) {
			cs_move_id = rs.getBigDecimal("id");
			cs_move_idstring = String.valueOf(cs_move_id);
			TIMEST = rs.getString("TIMEST");
			mty_cd = rs.getString("MTY_CD");
			mry_cd = rs.getString("MRY_CD");
			ac_dt_tm = rs.getString("ac_dt_tm");

			String[][] insmove = { { "ID", prop.getProperty("clntid"), prop.getProperty("CNO"), prop.getProperty("mty_cd"), prop.getProperty("mry_cd"), prop.getProperty("ac_dt_tm"), "ENT_DT" },
					{ cs_move_idstring, id, CNO, mty_cd, mry_cd, ac_dt_tm, TIMEST } };
			m = MarkupHelper.createTable(insmove);
			childTest.log(Status.INFO, m);

			insdmove.add(cs_move_idstring);
			insdmove.add(id);
			insdmove.add(CNO);
			insdmove.add(mty_cd);
			insdmove.add(mry_cd);
			insdmove.add(ac_dt_tm);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), "NULL");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: ORDS GET", colour.BLUE));
			String datetime = or_get.trans_or_get(CNO, prop.getProperty("trans_delete_cd"), gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_trans();	
			
			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, CNO, prop.getProperty("trans_delete_cd"), datetime);

			deleteevent.delete();

		}

		return insdmove;
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
>>>>>>> Sync with Master
=======
>>>>>>> Dev
