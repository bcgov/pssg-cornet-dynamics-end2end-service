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
import CTD_ContractTests.Loc_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Extent_Report.extentreport;

public class loc_event extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public delete_events deleteevent = new delete_events();

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";

	Loc_Event_verify_event unprocessed = new Loc_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
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

	@Test()
	public void locaevent() throws SQLException, ParseException, IOException, ClassNotFoundException {

		parentTest = extent.createTest(prop.getProperty("locaevent_parentextent"));

		parentTest.assignCategory(prop.getProperty("locaevent_category"));
		
		Statement s = conn.createStatement();
		int count = 0;
		int maxtries = 2;

		True:
		while (true) {
			try {

				
				System.out.println("..");

				int i = 0;

				String id = null;

				LinkedHashSet<String> idandtimest = new LinkedHashSet<String>();

				for (i = 1; i <= 3; i++) {
					id = parentinsert(i);
					updateloc_id_root(id);
					updateeff_dt(id);
					updateexp_dt(id);
					updateloty_code(id, i);
					idandtimest = updateloty_codeto(id, i);
					parent_delete(idandtimest, i);
					break True;
				}
			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("location_catch_delete"));
				conn.commit();
				deleteevent.delete();
				if (++count == maxtries)
					throw e;
			}
		}

	}

	private void parent_delete(LinkedHashSet<String> idandtimest, int deletefor)
			throws SQLException, IOException, ParseException {
		// TODO Auto-generated method stub
		Statement s = conn.createStatement();

		switch (deletefor) {
		case 1:
			childTest = parentTest.createNode(prop.getProperty("delil"));
			break;

		case 2:
			childTest = parentTest.createNode(prop.getProperty("delcc"));
			break;

		case 3:
			childTest = parentTest.createNode(prop.getProperty("delyi"));
			break;

		/*
		 * case 4: childTest = parentTest.createNode("Deleting CCYL"); break;
		 */
		}

		Iterator<String> it = idandtimest.iterator();
		String TIMEST = it.next();
		String idstring = it.next();
		String TYPE = "LOCATION";

		childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
		s.executeQuery(prop.getProperty("parent_delete_onedelete") + "" + idstring + "");
		conn.commit();

		childTest.log(Status.INFO, "The record with id " + idstring + " was deleted");

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

		String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
		or_get ords = new or_get();

		String datetime = ords.loc_or_get(idstring, null, null, null, null, null, gd);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_loc();

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

		s.executeQuery(prop.getProperty("clear_message_elements"));
		conn.commit();
		s.executeQuery(prop.getProperty("clear_event_messages"));
		conn.commit();

		s.close();
		childTest.pass("Test Passed");
	}

	private String parentinsert(int insertfor) throws SQLException, ParseException, IOException {
		// TODO Auto-generated method stub
		Statement s = conn.createStatement();
		ResultSet rs = null;

		switch (insertfor) {
		case 1:
			childTest = parentTest.createNode(prop.getProperty("insil"));
			s.executeQuery(prop.getProperty("parentinsert_ilinsert"));
			conn.commit();
			rs = s.executeQuery(prop.getProperty("parentinsert_ilselect"));
			break;

		case 2:
			childTest = parentTest.createNode(prop.getProperty("inscc"));
			s.executeQuery(prop.getProperty("parentinsert_ccinsert"));
			conn.commit();
			rs = s.executeQuery(prop.getProperty("parentinsert_ccselect"));
			break;

		case 3:
			childTest = parentTest.createNode(prop.getProperty("insyi"));
			s.executeQuery(prop.getProperty("parentinsert_yiinsert"));
			conn.commit();
			rs = s.executeQuery("parentinsert_yiselect");
			break;

		/*
		 * case 4: childTest = parentTest.createNode("Inserting CCYL");
		 * s.executeQuery(prop.getProperty("parentinsert_CCYLinsert")); conn.commit();
		 * rs = s.executeQuery(prop.getProperty("parentinsert_CCYLselect"); break;
		 */
		}

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		String loc_id = null;
		String idstring = null;

		Markup m;

		if (rs.next()) {

			id = rs.getBigDecimal("id");
			idstring = String.valueOf(id);
			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));

			childTest.log(Status.INFO, "The following were inserted");
			String[][] insertiloclog = { { "ID", "CODE", "DESC", "eff_dt", "exp_dt", "loc_id_root", "ent_dt" },
					{ idstring, code, desc, eff_dt, exp_dt, null, TIMEST } };
			m = MarkupHelper.createTable(insertiloclog);
			childTest.log(Status.INFO, m);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, code, desc, loc_id, eff_dt, exp_dt, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();

			s.close();
			rs.close();
			childTest.pass("Test Passed");

		}

		return idstring;
	}

	private void updateloc_id_root(String idstring) throws SQLException, ParseException, IOException {
		Statement s = conn.createStatement();
		ResultSet rs = null;

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		BigDecimal loca_id = null;
		String loca_idString = null;

		childTest = parentTest.createNode(prop.getProperty("updateloc_id_root_child_onetest"));
		s.executeQuery(prop.getProperty("updateloca_id_root_oneupdate") + "" + idstring + "");
		conn.commit();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
		childTest.log(Status.INFO, "The loc_id_root is updated from null to 877874 for id \"" + idstring + "\"");

		rs = s.executeQuery(prop.getProperty("updateloca_id_root_oneselect") + "" + idstring + "");

		if (rs.next()) {

			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");
			loca_id = rs.getBigDecimal("root");
			loca_idString = String.valueOf(loca_id);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, code, desc, loca_idString, eff_dt, exp_dt, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();
			childTest.pass("Test Passed");
		}

		s.close();
		rs.close();
	}

	private void updateeff_dt(String idstring) throws SQLException, ParseException, IOException {
		Statement s = conn.createStatement();
		ResultSet rs = null;

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		BigDecimal loca_id = null;
		String loca_idString = null;

		childTest = parentTest.createNode("Update eff_dt");
		s.executeQuery(prop.getProperty("updateeffective_dt_oneupdate") + "" + idstring + "");
		conn.commit();
		childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
		childTest.log(Status.INFO,
				"The effective_dt is updated from 01-Apr-2017 to 10-Apr-2017 for id \"" + idstring + "\"");

		rs = s.executeQuery(prop.getProperty("updateeffective_dt_oneselect") + "" + idstring + "");

		if (rs.next()) {

			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");
			loca_id = rs.getBigDecimal("root");
			loca_idString = String.valueOf(loca_id);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, code, desc, loca_idString, eff_dt, exp_dt, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();
			childTest.pass("Test Passed");
		}

		s.close();
		rs.close();
	}

	private void updateexp_dt(String idstring) throws SQLException, ParseException, IOException {
		Statement s = conn.createStatement();
		ResultSet rs = null;

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		BigDecimal loca_id = null;
		String loca_idString = null;

		childTest = parentTest.createNode("Update exp_dt");
		s.executeQuery(prop.getProperty("updateexpiry_dt_oneupdate") + "" + idstring + "");
		conn.commit();
		childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
		childTest.log(Status.INFO,
				"The expiry_dt is updated from 01-Apr-2019 to 10-Apr-2019 for id \"" + idstring + "\"");

		rs = s.executeQuery(prop.getProperty("updateexpiry_dt_oneselect") + "" + idstring + "");

		if (rs.next()) {

			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");
			loca_id = rs.getBigDecimal("root");
			loca_idString = String.valueOf(loca_id);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String guid = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, code, desc, loca_idString, eff_dt, exp_dt, guid);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();

			childTest.pass("Test Passed");
		}

		s.close();
		rs.close();
	}

	private void updateloty_code(String idstring, int updatefor) throws SQLException, ParseException, IOException {
		Statement s = conn.createStatement();
		ResultSet rs = null;

		switch (updatefor) {
		case 1:
			childTest = parentTest.createNode(prop.getProperty("upil"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+prop.getProperty("locinst")+" for id \"" + idstring + "\"");
			break;

		case 2:
			childTest = parentTest.createNode(prop.getProperty("upcc"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+prop.getProperty("cclinst")+" for id \"" + idstring + "\"");
			break;

		case 3:
			childTest = parentTest.createNode(prop.getProperty("upyi"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+prop.getProperty("ylinst")+" for id \"" + idstring + "\"");
			break;

		/*
		 * case 4: childTest = parentTest.createNode("Updating LOTY_CD-CCYL");
		 * childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger",
		 * colour.BLUE)); childTest.log(Status.INFO,
		 * "The loty_cd is updated from for id \""+idstring+"\""); break;
		 */
		}

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		String type_code = null;
		BigDecimal loca_id = null;
		String loca_idString = null;

		s.executeQuery(prop.getProperty("updateloty_code_oneupdate") + "" + idstring + "");
		conn.commit();

		rs = s.executeQuery(prop.getProperty("updateloty_code_oneselect") + "" + idstring + "");

		if (rs.next()) {
			type_code = rs.getString("type_code");
			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");
			loca_id = rs.getBigDecimal("root");
			loca_idString = String.valueOf(loca_id);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, null, null, null, null, null, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();

			childTest.pass("Test Passed");
		}

		s.close();
		rs.close();

	}

	private LinkedHashSet<String> updateloty_codeto(String idstring, int updatefor)
			throws SQLException, ParseException, IOException {
		Statement s = conn.createStatement();
		ResultSet rs = null;

		switch (updatefor) {
		case 1:
			childTest = parentTest.createNode(prop.getProperty("uptoil"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+ prop.getProperty("inil")+" for id \"" + idstring + "\"");
			s.executeQuery(prop.getProperty("up_ilupdate") + "" + idstring + "");
			conn.commit();
			break;

		case 2:
			childTest = parentTest.createNode(prop.getProperty("uptocc"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+prop.getProperty("incc")+" for id \"" + idstring + "\"");
			s.executeQuery(prop.getProperty("up_ccupdate") + "" + idstring + "");
			conn.commit();
			break;

		case 3:
			childTest = parentTest.createNode(prop.getProperty("uptoyi"));
			childTest.log(Status.INFO, MarkupHelper.createLabel("Event Trigger", colour.BLUE));
			childTest.log(Status.INFO, "The loty_code is updated from "+prop.getProperty("inyi")+" for id \"" + idstring + "\"");
			s.executeQuery(prop.getProperty("up_yiupdate") + "" + idstring + "");
			conn.commit();
			break;

		/*
		 * case 4: childTest = parentTest.createNode("Updating LOTY_CD to CCYL");
		 * childTest.log(Status.INFO,
		 * "The loty_code is updated from INST to CCYL for id \""+idstring+"\"");
		 * s.executeQuery(prop.getProperty("updateloty_cdto_CCYLupdate")+""+idstring+
		 * ""); conn.commit(); break;
		 */
		}

		BigDecimal id = null;
		String code = null;
		String desc = null;
		String eff_dt = null;
		String exp_dt = null;
		String TIMEST = null;
		String TYPE = "LOCATION";
		String type_code = null;
		BigDecimal loca_id = null;
		String loca_idString = null;

		LinkedHashSet<String> timestandexpirt_dt = new LinkedHashSet<String>();

		rs = s.executeQuery(prop.getProperty("updateloty_codeto_oneselect") + "" + idstring + "");

		if (rs.next()) {
			type_code = rs.getString("type_code");
			code = rs.getString("code");
			desc = rs.getString("descr");
			eff_dt = rs.getString("eff_dt");
			exp_dt = rs.getString("exp_dt");
			TIMEST = rs.getString("TIMEST");
			loca_id = rs.getBigDecimal("root");
			loca_idString = String.valueOf(loca_id);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));

			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
			or_get ords = new or_get();

			String datetime = ords.loc_or_get(idstring, code, desc, loca_idString, eff_dt, exp_dt, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_loc();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);

			childTest.pass("Test Passed");

			s.executeQuery(prop.getProperty("clear_message_elements"));
			conn.commit();
			s.executeQuery(prop.getProperty("clear_event_messages"));
			conn.commit();

			timestandexpirt_dt.add(TIMEST);
			timestandexpirt_dt.add(idstring);
		}

		s.close();
		rs.close();

		return timestandexpirt_dt;
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
