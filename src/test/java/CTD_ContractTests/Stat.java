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
import CTD_ContractTests.Stat_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tombstoneanddiscliplinarychargenotification;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class Stat extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	Stat_Event_verify_event unprocessed = new Stat_Event_verify_event();
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

	@Test
	public void insertupdateanddelete() throws SQLException, ParseException, IOException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("stat_TYPE");

		String gd = null;
		BigDecimal id;
		String idstring = null;
		parentTest = extent.createTest(prop.getProperty("Stat_insertupdateanddelete_parent_extent"));
		parentTest.assignCategory(prop.getProperty("stat_event"));
		int count = 0;
		int maxtries = 2;

		try {
			exit: while (true) {
				try {

					System.out.println("..");

					childTest = parentTest.createNode(prop.getProperty("Stat_insertupdateanddelete_onechild"));

					s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneinsert"));
					conn.commit();

					String LTY_CD = prop.getProperty("stat_lty_cd_val");
					String PTY_CD = prop.getProperty("stat_PTY_CD_cd_val");
					String DESCR = prop.getProperty("stat_DESCR_val");
					String sc_no = prop.getProperty("stat_sc_no_val");
					String sub_sc_no = prop.getProperty("stat_sub_sc_no_val");
					String sub_para_no = prop.getProperty("stat_sub_para_no_val");
					String para_no = prop.getProperty("stat_para_no_val");

					rs = s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneselect"));

					if (rs.next()) {

						id = rs.getBigDecimal("id");
						idstring = String.valueOf(id);
						TIMEST = rs.getString("TIMEST");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted into cms_statutes");
						String[][] requestchosen = {
								{ prop.getProperty("sta_id"), prop.getProperty("sta_lty_cd"),
										prop.getProperty("sta_pty_cd"), prop.getProperty("sta_descr_cd"), "TIMEST" },
								{ idstring, LTY_CD, PTY_CD, DESCR, TIMEST } };
						m = MarkupHelper.createTable(requestchosen);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get ords = new or_get();

						String datetime = or_get.stat_or_get(idstring, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_stat();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

						deleteevent.delete();

						String updated_lety_cd = updatelty_cd(sc_no, sub_sc_no, sub_para_no, para_no, idstring);

						String updated_sc_no = updatesc_no(sc_no, sub_sc_no, sub_para_no, para_no, idstring);

						String updated_sub_sc_no = updatesub_sc_no(updated_sc_no, sub_sc_no, sub_para_no, para_no,
								idstring);

						String updated_sub_para_no = updatesub_para_no(updated_sc_no, updated_sub_sc_no, sub_para_no,
								para_no, idstring);

						String updated_para_no = update_para_no(updated_sc_no, updated_sub_sc_no, updated_sub_para_no,
								para_no, idstring);

						String updated_eff_date = update_eff_dt(updated_sc_no, updated_sub_sc_no, updated_sub_para_no,
								updated_para_no, idstring);

						LinkedHashSet<String> TIMESTD = update_exp_dt(updated_sc_no, updated_sub_sc_no,
								updated_sub_para_no, updated_para_no, idstring);

						Iterator<String> it = TIMESTD.iterator();
						String TIMESTDel = it.next();
						String exp_date = it.next();

						delete(TIMESTDel, idstring, exp_date, updated_eff_date, updated_para_no, updated_sub_para_no,
								updated_sub_sc_no, updated_sc_no, LTY_CD, PTY_CD, DESCR);
						break exit;
					}
				} catch (SQLException e) {
					if (++count == maxtries)
						throw e;
					else
						s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_catch_del"));

				}
			}
		} finally {
			rs.close();
		}

	}

	private String updatelty_cd(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

		childTest = parentTest.createNode(prop.getProperty("st_up_lt"));

		Statement s = conn.createStatement();
		ResultSet updatelty_cd;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("updatelty_cd_oneupdate_onepart") + "'" + sc_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_twopart") + "'" + sub_sc_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_threepart") + "'" + sub_para_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_fourpart") + "'" + para_no + "'");
		conn.commit();

		String updated_lty_cd = prop.getProperty("updatelty_cd_updated_lty_cd_val");

		updatelty_cd = s.executeQuery(prop.getProperty("updatelty_cd_oneselect"));

		if (updatelty_cd.next()) {
			TIMEST = updatelty_cd.getString("TIMEST");
			childTest.log(Status.INFO,
					"The " + prop.getProperty("updatelty_cd_lty_cd") + "was updated to " + updated_lty_cd + " from "
							+ prop.getProperty("updatelty_cd_beforeupdate_lty_cd_val") + " for ID=" + id + " at upd_dt="
							+ TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
		return updated_lty_cd;

	}

	private String updatesc_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_sc"));

		Statement s = conn.createStatement();
		ResultSet updatesc_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_sc_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_sc_no = prop.getProperty("stat_upsc_no_val");

		updatesc_no = s.executeQuery(prop.getProperty("stat_updatesc_no_oneselect"));

		if (updatesc_no.next()) {
			TIMEST = updatesc_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sc_no") + " was updated to " + updated_sc_no
					+ " from " + sc_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);
			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
		return updated_sc_no;

	}

	private String updatesub_sc_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_su_sc"));

		Statement s = conn.createStatement();
		ResultSet updatesubsc_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		String updated_sub_sc_no = prop.getProperty("updated_sub_sc_no_val");

		s.executeQuery(prop.getProperty("stat_updatesub_sc_no_oneupdate") + "" + id + "");
		conn.commit();
		
		updatesubsc_no = s.executeQuery(prop.getProperty("stat_updatesub_sc_no_oneselect_onepart") + "'" + sc_no + "' "
				+ prop.getProperty("stat_updatesub_sc_no_oneselect_twopart") + "'" + updated_sub_sc_no + "'"
				+ prop.getProperty("stat_updatesub_sc_no_oneselect_threepart"));

		if (updatesubsc_no.next()) {
			TIMEST = updatesubsc_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sub_sc_no") + " was updated to " + updated_sub_sc_no
					+ " from " + sub_sc_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_sub_sc_no;
	}

	private String updatesub_para_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_su_pa"));

		Statement s = conn.createStatement();
		ResultSet updatesubpara_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_updatesub_para_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_sub_para_no = prop.getProperty("updated_sub_para_no_val");

		updatesubpara_no = s.executeQuery(prop.getProperty("stat_updatesub_para_no_oneselect"));

		if (updatesubpara_no.next()) {
			TIMEST = updatesubpara_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sub_para_no") + "  was updated to "
					+ updated_sub_para_no + " from " + sub_para_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_sub_para_no;
	}

	private String update_para_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_pa"));

		Statement s = conn.createStatement();
		ResultSet updatepara_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_para_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_para_no = prop.getProperty("updated_para_no_val");

		updatepara_no = s.executeQuery(prop.getProperty("stat_update_para_no_oneselect"));

		if (updatepara_no.next()) {
			TIMEST = updatepara_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("para_no") + " was updated to " + updated_para_no
					+ " from " + para_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_para_no;
	}

	private String update_eff_dt(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_ef"));

		Statement s = conn.createStatement();
		ResultSet updateeff_dt;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_eff_dt_oneupdate") + "" + id + "");
		conn.commit();

		String updated_eff_date = prop.getProperty("updated_eff_date_val");

		updateeff_dt = s.executeQuery(prop.getProperty("stat_update_eff_dt_oneselect") + "" + id + "");

		if (updateeff_dt.next()) {
			TIMEST = updateeff_dt.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("eff_dt") + " was updated to " + updated_eff_date
					+ " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_eff_date;
	}

	private LinkedHashSet<String> update_exp_dt(String sc_no, String sub_sc_no, String sub_para_no, String para_no,
			String id) throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_exp"));

		Statement s = conn.createStatement();
		ResultSet updateexp_dt;

		String TIMEST = null;
		String TIMESTD = null;
		String TYPE = prop.getProperty("stat_TYPE");

		LinkedHashSet<String> timestandexpirt_dt = new LinkedHashSet<String>();

		s.executeQuery(prop.getProperty("stat_update_exp_dt") + "" + id + "");
		conn.commit();

		String updated_exp_dt = prop.getProperty("updated_exp_dt_val");

		updateexp_dt = s.executeQuery(prop.getProperty("stat_update_para_no_oneselect"));

		if (updateexp_dt.next()) {
			TIMEST = updateexp_dt.getString("TIMEST");
			TIMESTD = TIMEST;
			childTest.log(Status.INFO, "The " + prop.getProperty("exp_dt") + " was updated to " + updated_exp_dt
					+ " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			timestandexpirt_dt.add(TIMESTD);
			timestandexpirt_dt.add(updated_exp_dt);
		}

		return timestandexpirt_dt;
	}

	private void delete(String TIMEST, String id, String exp_dt, String updated_eff_dt, String updated_para_no,
			String updated_sub_para_no, String updated_sub_sc_no, String updated_sc_no, String LTY_CD, String PTY_CD,
			String DESCR) throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode("Delete");

		Statement s = conn.createStatement();

		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_delete_onedel") + "" + id + "");
		conn.commit();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

		String datetime = or_get.stat_del_or_get(id, gd, null, null, null, null, null, null, null, null, null);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_stat();

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

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
import CTD_ContractTests.Stat_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tombstoneanddiscliplinarychargenotification;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class Stat extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	Stat_Event_verify_event unprocessed = new Stat_Event_verify_event();
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

	@Test
	public void insertupdateanddelete() throws SQLException, ParseException, IOException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("stat_TYPE");

		String gd = null;
		BigDecimal id;
		String idstring = null;
		parentTest = extent.createTest(prop.getProperty("Stat_insertupdateanddelete_parent_extent"));
		parentTest.assignCategory(prop.getProperty("stat_event"));
		int count = 0;
		int maxtries = 2;

		try {
			exit: while (true) {
				try {

					System.out.println("..");

					childTest = parentTest.createNode(prop.getProperty("Stat_insertupdateanddelete_onechild"));

					s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneinsert"));
					conn.commit();

					String LTY_CD = prop.getProperty("stat_lty_cd_val");
					String PTY_CD = prop.getProperty("stat_PTY_CD_cd_val");
					String DESCR = prop.getProperty("stat_DESCR_val");
					String sc_no = prop.getProperty("stat_sc_no_val");
					String sub_sc_no = prop.getProperty("stat_sub_sc_no_val");
					String sub_para_no = prop.getProperty("stat_sub_para_no_val");
					String para_no = prop.getProperty("stat_para_no_val");

					rs = s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_oneselect"));

					if (rs.next()) {

						id = rs.getBigDecimal("id");
						idstring = String.valueOf(id);
						TIMEST = rs.getString("TIMEST");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted into cms_statutes");
						String[][] requestchosen = {
								{ prop.getProperty("sta_id"), prop.getProperty("sta_lty_cd"),
										prop.getProperty("sta_pty_cd"), prop.getProperty("sta_descr_cd"), "TIMEST" },
								{ idstring, LTY_CD, PTY_CD, DESCR, TIMEST } };
						m = MarkupHelper.createTable(requestchosen);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get ords = new or_get();

						String datetime = or_get.stat_or_get(idstring, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_stat();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

						deleteevent.delete();

						String updated_lety_cd = updatelty_cd(sc_no, sub_sc_no, sub_para_no, para_no, idstring);

						String updated_sc_no = updatesc_no(sc_no, sub_sc_no, sub_para_no, para_no, idstring);

						String updated_sub_sc_no = updatesub_sc_no(updated_sc_no, sub_sc_no, sub_para_no, para_no,
								idstring);

						String updated_sub_para_no = updatesub_para_no(updated_sc_no, updated_sub_sc_no, sub_para_no,
								para_no, idstring);

						String updated_para_no = update_para_no(updated_sc_no, updated_sub_sc_no, updated_sub_para_no,
								para_no, idstring);

						String updated_eff_date = update_eff_dt(updated_sc_no, updated_sub_sc_no, updated_sub_para_no,
								updated_para_no, idstring);

						LinkedHashSet<String> TIMESTD = update_exp_dt(updated_sc_no, updated_sub_sc_no,
								updated_sub_para_no, updated_para_no, idstring);

						Iterator<String> it = TIMESTD.iterator();
						String TIMESTDel = it.next();
						String exp_date = it.next();

						delete(TIMESTDel, idstring, exp_date, updated_eff_date, updated_para_no, updated_sub_para_no,
								updated_sub_sc_no, updated_sc_no, LTY_CD, PTY_CD, DESCR);
						break exit;
					}
				} catch (SQLException e) {
					if (++count == maxtries)
						throw e;
					else
						s.executeQuery(prop.getProperty("Stat_insertupdateanddelete_catch_del"));

				}
			}
		} finally {
			rs.close();
		}

	}

	private String updatelty_cd(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

		childTest = parentTest.createNode(prop.getProperty("st_up_lt"));

		Statement s = conn.createStatement();
		ResultSet updatelty_cd;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("updatelty_cd_oneupdate_onepart") + "'" + sc_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_twopart") + "'" + sub_sc_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_threepart") + "'" + sub_para_no + "'"
				+ prop.getProperty("updatelty_cd_oneupdate_fourpart") + "'" + para_no + "'");
		conn.commit();

		String updated_lty_cd = prop.getProperty("updatelty_cd_updated_lty_cd_val");

		updatelty_cd = s.executeQuery(prop.getProperty("updatelty_cd_oneselect"));

		if (updatelty_cd.next()) {
			TIMEST = updatelty_cd.getString("TIMEST");
			childTest.log(Status.INFO,
					"The " + prop.getProperty("updatelty_cd_lty_cd") + "was updated to " + updated_lty_cd + " from "
							+ prop.getProperty("updatelty_cd_beforeupdate_lty_cd_val") + " for ID=" + id + " at upd_dt="
							+ TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
		return updated_lty_cd;

	}

	private String updatesc_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_sc"));

		Statement s = conn.createStatement();
		ResultSet updatesc_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_sc_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_sc_no = prop.getProperty("stat_upsc_no_val");

		updatesc_no = s.executeQuery(prop.getProperty("stat_updatesc_no_oneselect"));

		if (updatesc_no.next()) {
			TIMEST = updatesc_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sc_no") + " was updated to " + updated_sc_no
					+ " from " + sc_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);
			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

		}
		return updated_sc_no;

	}

	private String updatesub_sc_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_su_sc"));

		Statement s = conn.createStatement();
		ResultSet updatesubsc_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		String updated_sub_sc_no = prop.getProperty("updated_sub_sc_no_val");

		s.executeQuery(prop.getProperty("stat_updatesub_sc_no_oneupdate") + "" + id + "");
		conn.commit();
		
		updatesubsc_no = s.executeQuery(prop.getProperty("stat_updatesub_sc_no_oneselect_onepart") + "'" + sc_no + "' "
				+ prop.getProperty("stat_updatesub_sc_no_oneselect_twopart") + "'" + updated_sub_sc_no + "'"
				+ prop.getProperty("stat_updatesub_sc_no_oneselect_threepart"));

		if (updatesubsc_no.next()) {
			TIMEST = updatesubsc_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sub_sc_no") + " was updated to " + updated_sub_sc_no
					+ " from " + sub_sc_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_sub_sc_no;
	}

	private String updatesub_para_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_su_pa"));

		Statement s = conn.createStatement();
		ResultSet updatesubpara_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_updatesub_para_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_sub_para_no = prop.getProperty("updated_sub_para_no_val");

		updatesubpara_no = s.executeQuery(prop.getProperty("stat_updatesub_para_no_oneselect"));

		if (updatesubpara_no.next()) {
			TIMEST = updatesubpara_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("sub_para_no") + "  was updated to "
					+ updated_sub_para_no + " from " + sub_para_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_sub_para_no;
	}

	private String update_para_no(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_pa"));

		Statement s = conn.createStatement();
		ResultSet updatepara_no;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_para_no_oneupdate") + "" + id + "");
		conn.commit();

		String updated_para_no = prop.getProperty("updated_para_no_val");

		updatepara_no = s.executeQuery(prop.getProperty("stat_update_para_no_oneselect"));

		if (updatepara_no.next()) {
			TIMEST = updatepara_no.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("para_no") + " was updated to " + updated_para_no
					+ " from " + para_no + " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_para_no;
	}

	private String update_eff_dt(String sc_no, String sub_sc_no, String sub_para_no, String para_no, String id)
			throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_ef"));

		Statement s = conn.createStatement();
		ResultSet updateeff_dt;

		String TIMEST;
		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_update_eff_dt_oneupdate") + "" + id + "");
		conn.commit();

		String updated_eff_date = prop.getProperty("updated_eff_date_val");

		updateeff_dt = s.executeQuery(prop.getProperty("stat_update_eff_dt_oneselect") + "" + id + "");

		if (updateeff_dt.next()) {
			TIMEST = updateeff_dt.getString("TIMEST");
			childTest.log(Status.INFO, "The " + prop.getProperty("eff_dt") + " was updated to " + updated_eff_date
					+ " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();
		}

		return updated_eff_date;
	}

	private LinkedHashSet<String> update_exp_dt(String sc_no, String sub_sc_no, String sub_para_no, String para_no,
			String id) throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode(prop.getProperty("st_up_exp"));

		Statement s = conn.createStatement();
		ResultSet updateexp_dt;

		String TIMEST = null;
		String TIMESTD = null;
		String TYPE = prop.getProperty("stat_TYPE");

		LinkedHashSet<String> timestandexpirt_dt = new LinkedHashSet<String>();

		s.executeQuery(prop.getProperty("stat_update_exp_dt") + "" + id + "");
		conn.commit();

		String updated_exp_dt = prop.getProperty("updated_exp_dt_val");

		updateexp_dt = s.executeQuery(prop.getProperty("stat_update_para_no_oneselect"));

		if (updateexp_dt.next()) {
			TIMEST = updateexp_dt.getString("TIMEST");
			TIMESTD = TIMEST;
			childTest.log(Status.INFO, "The " + prop.getProperty("exp_dt") + " was updated to " + updated_exp_dt
					+ " for ID=" + id + " at upd_dt=" + TIMEST + "");

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
			String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
			n.event_notification();

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

			String datetime = or_get.stat_or_get(id, gd);

			childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
			da.da_stat();

			childTest.log(Status.INFO,
					MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
			unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
			childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

			deleteevent.delete();

			timestandexpirt_dt.add(TIMESTD);
			timestandexpirt_dt.add(updated_exp_dt);
		}

		return timestandexpirt_dt;
	}

	private void delete(String TIMEST, String id, String exp_dt, String updated_eff_dt, String updated_para_no,
			String updated_sub_para_no, String updated_sub_sc_no, String updated_sc_no, String LTY_CD, String PTY_CD,
			String DESCR) throws SQLException, ParseException, IOException, ClassNotFoundException {

		childTest = parentTest.createNode("Delete");

		Statement s = conn.createStatement();

		String TYPE = prop.getProperty("stat_TYPE");

		s.executeQuery(prop.getProperty("stat_delete_onedel") + "" + id + "");
		conn.commit();

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
		String gd = unprocessed.exampleGetTest(TIMEST, TYPE, id, "NULL", 1);

		childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
		n.event_notification();

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

		String datetime = or_get.stat_del_or_get(id, gd, null, null, null, null, null, null, null, null, null);

		childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
		da.da_stat();

		childTest.log(Status.INFO,
				MarkupHelper.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
		unprocessed.exampleGetTest(TIMEST, TYPE, id, datetime, 2);
		childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

		deleteevent.delete();

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
