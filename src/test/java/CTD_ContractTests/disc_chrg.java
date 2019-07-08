<<<<<<< HEAD
<<<<<<< master
=======
>>>>>>> Dev
package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Properties;

import org.testng.Assert;
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
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tombstoneanddiscliplinarychargenotification;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class disc_chrg extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	
	public String birthdate(String birthdt) {
		String date = birthdt;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		LocalDate localDate = dat.toLocalDate();
		int monthvalue = localDate.getMonthValue();
		String month = getMonth(monthvalue);
		String birthdate = localDate.getDayOfMonth() + "-" + month + "-" + localDate.getYear();
		return birthdate;
	}
	
	public String getMonth(int monthvalue) {
		String month = null;

		switch (monthvalue) {
		case 1:
			month = "Jan";
			break;

		case 2:
			month = "Feb";
			break;

		case 3:
			month = "Mar";
			break;

		case 4:
			month = "Apr";
			break;

		case 5:
			month = "May";
			break;

		case 6:
			month = "Jun";
			break;

		case 7:
			month = "Jul";
			break;

		case 8:
			month = "Aug";
			break;

		case 9:
			month = "Sep";
			break;

		case 10:
			month = "Oct";
			break;

		case 11:
			month = "Nov";
			break;

		case 12:
			month = "Dec";
			break;
		}

		return month;

	}

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
	
	public String birthdatechanged(String birthdt) {
		String date = birthdt;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		LocalDate localDate = dat.toLocalDate();
		int datechanged;
		int monthvalue = localDate.getMonthValue();
		String month = getMonth(monthvalue);
		if (localDate.getDayOfMonth() >= 28) {
			datechanged = localDate.getDayOfMonth() - 1;
		} else {
			datechanged = localDate.getDayOfMonth() + 1;
		}
		String birthdate = datechanged + "-" + month + "-" + localDate.getYear();
		return birthdate;
	}

	@Test
	public void reg_id() throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String reg_idchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {

			parentTest = extent.createTest(prop.getProperty("reg_id_parent_extent"));
			parentTest.assignCategory(prop.getProperty("discategory"));

			System.out.println("..");

			childTest = parentTest.createNode(prop.getProperty("reg_id_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("reg_id_oneselect"));

			if (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"),
						prop.getProperty("rd"), prop.getProperty("dischrg_id") }, { CNO, clid, reg_id, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				if (reg_id.equals("90")) {
					s.executeQuery(prop.getProperty("reg_id_oneupdate") + "" + disc_chg_id + "");
					conn.commit();
				} else {
					s.executeQuery(prop.getProperty("reg_id_secondupdate") + "" + disc_chg_id + "");
					conn.commit();
				}

				extentreg_id = s.executeQuery(prop.getProperty("reg_id_afterupdateselect") + "" + disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					reg_idchanged = extentreg_id.getString("reg_id");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_idchanged, disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_idchanged, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					or_get or = new or_get();
					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					String datetime = or_get.disc_chg_or_get(CNO, reg_idchanged, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_idchanged, datetime);

					deleteevent.delete();

				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			} else {
				childTest.log(Status.FAIL, "Could not retrieve a result set to chose a client to update");
				Assert.assertTrue(false, "Automation Test Error in Code");
			}
		} finally {
			s.executeQuery(prop.getProperty("reg_id_finallyupdate_onehalf") + "'" + reg_id + "'"
					+ prop.getProperty("reg_id_finallyupdate_secondhalf") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id" }, alwaysRun = true)
	public void sld_ynntoy() throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String Sld_yn = null;
		String Sld_ynchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {
			parentTest = extent.createTest(prop.getProperty("sld_ynntoy_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("sld_ynntoy_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("sld_ynntoy_oneselect"));

			disc_chgs: while (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				Sld_yn = rs.getString("sld_yn");

				if (Sld_yn.equals("N")) {
					String[][] requestchosen = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("syn"), prop.getProperty("dischrg_id") },
							{ CNO, clid, clid, Sld_yn, disc_chg_id } };
					m = MarkupHelper.createTable(requestchosen);
					childTest.log(Status.INFO, m);

					s.executeQuery(prop.getProperty("sld_ynntoy_oneupdate") + "" + disc_chg_id + "");
					conn.commit();

					extentreg_id = s.executeQuery(prop.getProperty("sld_ynntoy_twoselect") + "" + disc_chg_id + "");

					if (extentreg_id.next()) {
						CNO = extentreg_id.getString("CNO");
						clid = extentreg_id.getString("clid");
						Sld_ynchanged = extentreg_id.getString("sld_yn");
						disc_chg_id = extentreg_id.getString("disc_chg_id");
						TIMEST = extentreg_id.getString("TIMEST");

						String[][] changedrequest = {
								{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
										prop.getProperty("syn"), prop.getProperty("dischrg_id"), "UPD_DTM" },
								{ CNO, clid, reg_id, Sld_ynchanged, disc_chg_id, TIMEST } };
						m = MarkupHelper.createTable(changedrequest);
						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get or = new or_get();

						String datetime = or_get.disc_chg_or_get(CNO, reg_id, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

						deleteevent.delete();

						break disc_chgs;
					} else {
						childTest.log(Status.FAIL, "query error");
						Assert.assertTrue(false, "Automation Test Error in Code");
					}

				}

			}

		} finally {
			s.executeQuery(prop.getProperty("sld_ynntoy_finallyupdate") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy" }, alwaysRun = true)
	public void sld_ynyton() throws SQLException, IOException, ParseException, ClassNotFoundException {

		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String Sld_yn = null;
		String Sld_ynchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {
			parentTest = extent.createTest(prop.getProperty("sld_ynyton_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("reg_id_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			rs = s.executeQuery(prop.getProperty("sld_ynyton_oneselect"));

			disc_chgs: while (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				Sld_yn = rs.getString("sld_yn");

				if (Sld_yn.equals("Y")) {
					String[][] requestchosen = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("syn"), prop.getProperty("dischrg_id") },
							{ CNO, clid, clid, Sld_yn, disc_chg_id } };
					m = MarkupHelper.createTable(requestchosen);
					childTest.log(Status.INFO, m);

					s.executeQuery(prop.getProperty("sld_ynyton_oneupdate") + "" + disc_chg_id + "");
					conn.commit();

					extentreg_id = s.executeQuery(prop.getProperty("sld_ynyton_twoselect") + "" + disc_chg_id + "");

					if (extentreg_id.next()) {
						CNO = extentreg_id.getString("cno");
						clid = extentreg_id.getString("clid");
						Sld_ynchanged = extentreg_id.getString("sld_yn");
						disc_chg_id = extentreg_id.getString("disc_chg_id");
						TIMEST = extentreg_id.getString("TIMEST");

						String[][] changedrequest = {
								{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
										prop.getProperty("syn"), prop.getProperty("dischrg_id"), "UPD_DTM" },
								{ CNO, clid, clid, Sld_yn, disc_chg_id, TIMEST } };
						m = MarkupHelper.createTable(changedrequest);
						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();

						String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get or = new or_get();

						String datetime = or_get.disc_chg_or_get(CNO, reg_id, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

						deleteevent.delete();

						break disc_chgs;
					} else {
						childTest.log(Status.FAIL, "query error");
						Assert.assertTrue(false, "Automation Test Error in Code");
					}

				}

			}

		} finally {
			s.executeQuery(prop.getProperty("sld_ynyton_finallyupdate") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy", "sld_ynyton" }, alwaysRun = true)
	public void chg_dtchangedfromlast5yrstopast5yrs()
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		
		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String chg_dt = null;
		String chg_dtchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");
		String chgdt = null;
		String chgdtforchange = null;

	try {

			parentTest = extent.createTest(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_parent_extent"));
			
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneselect"));

			disc_chgs: if (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				chg_dt = rs.getString("chg_dt");
				chgdt = birthdate(chg_dt);

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
					prop.getProperty("chg_dt"), prop.getProperty("dischrg_id") },
						{ CNO, clid, reg_id, chg_dt, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				chgdtforchange = birthdatechanged(chg_dt);

				s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneupdate_onehalf") + "'"
						+ chgdtforchange + "'"
						+ prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneupdate_twohalf") + ""
						+ disc_chg_id + "");
				conn.commit();

				extentreg_id = s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_twoselect") + ""
						+ disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					chg_dtchanged = extentreg_id.getString("chg_dt");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
								prop.getProperty("chg_dt"), prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_id, chg_dtchanged,
									disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					or_get or = new or_get();

					String datetime = or.disc_chg_or_get(CNO, reg_id, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

					deleteevent.delete();

					break disc_chgs;
				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			}

		}finally {
			s.executeQuery("update cms_disciplinary_charges SET charge_dt='" + chgdt
					+ "' where disciplinary_charge_id=" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();
			s.close();
			

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy", "sld_ynyton" }, alwaysRun = true)
	public void charge_dtchangedpast5yrstolast5yrs()
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String chg_dt = null;
		String chg_dtchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = "DISC_CHRG";
		String chgdt = null;
		String chgdtforchange = null;

		try {
			parentTest = extent.createTest(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));
			tombstoneanddiscliplinarychargenotification n = new tombstoneanddiscliplinarychargenotification();
			Adapter da = new Adapter();

			childTest = parentTest.createNode(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			rs = s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_oneselect"));

			disc_chgs: if (rs.next()) {
				CNO = rs.getString("CNO");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				chg_dt = rs.getString("chg_dt");
				chgdt = birthdate(chg_dt);

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
					prop.getProperty("chg_dt"), prop.getProperty("dischrg_id")  },
						{ CNO, clid, reg_id, chg_dt, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				chgdtforchange = birthdatechanged(chg_dt);

				s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_oneupdate") + ""
						+ disc_chg_id + "");
				conn.commit();

				extentreg_id = s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_twoselect") + ""
						+ disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					chg_dtchanged = extentreg_id.getString("chg_dt");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
								prop.getProperty("chg_dt"), prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_id, chg_dtchanged,
									disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();

					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					or_get or = new or_get();

					String datetime = or.disc_chg_or_get(CNO, reg_id, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

					deleteevent.delete();

					break disc_chgs;
				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			}

		} finally {
			s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_finallyupdate_onehalf") + "'" + chgdt
					+ "'" + prop.getProperty("chg_dtchangedpast5yrstolast5yrs_finallyupdate_twohalf") + ""
					+ disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

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
<<<<<<< HEAD
=======
package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Properties;

import org.testng.Assert;
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
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_ContractTests.tombstoneanddiscliplinarychargenotification;
import CTD_ContractTests.tstone_Event_verify_event;
import CTD_Extent_Report.extentreport;

public class disc_chrg extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	
	public String birthdate(String birthdt) {
		String date = birthdt;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		LocalDate localDate = dat.toLocalDate();
		int monthvalue = localDate.getMonthValue();
		String month = getMonth(monthvalue);
		String birthdate = localDate.getDayOfMonth() + "-" + month + "-" + localDate.getYear();
		return birthdate;
	}
	
	public String getMonth(int monthvalue) {
		String month = null;

		switch (monthvalue) {
		case 1:
			month = "Jan";
			break;

		case 2:
			month = "Feb";
			break;

		case 3:
			month = "Mar";
			break;

		case 4:
			month = "Apr";
			break;

		case 5:
			month = "May";
			break;

		case 6:
			month = "Jun";
			break;

		case 7:
			month = "Jul";
			break;

		case 8:
			month = "Aug";
			break;

		case 9:
			month = "Sep";
			break;

		case 10:
			month = "Oct";
			break;

		case 11:
			month = "Nov";
			break;

		case 12:
			month = "Dec";
			break;
		}

		return month;

	}

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
	
	public String birthdatechanged(String birthdt) {
		String date = birthdt;
		java.sql.Date dat = java.sql.Date.valueOf(date);
		LocalDate localDate = dat.toLocalDate();
		int datechanged;
		int monthvalue = localDate.getMonthValue();
		String month = getMonth(monthvalue);
		if (localDate.getDayOfMonth() >= 28) {
			datechanged = localDate.getDayOfMonth() - 1;
		} else {
			datechanged = localDate.getDayOfMonth() + 1;
		}
		String birthdate = datechanged + "-" + month + "-" + localDate.getYear();
		return birthdate;
	}

	@Test
	public void reg_id() throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String reg_idchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {

			parentTest = extent.createTest(prop.getProperty("reg_id_parent_extent"));
			parentTest.assignCategory(prop.getProperty("discategory"));

			System.out.println("..");

			childTest = parentTest.createNode(prop.getProperty("reg_id_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("reg_id_oneselect"));

			if (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"),
						prop.getProperty("rd"), prop.getProperty("dischrg_id") }, { CNO, clid, reg_id, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				if (reg_id.equals("90")) {
					s.executeQuery(prop.getProperty("reg_id_oneupdate") + "" + disc_chg_id + "");
					conn.commit();
				} else {
					s.executeQuery(prop.getProperty("reg_id_secondupdate") + "" + disc_chg_id + "");
					conn.commit();
				}

				extentreg_id = s.executeQuery(prop.getProperty("reg_id_afterupdateselect") + "" + disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					reg_idchanged = extentreg_id.getString("reg_id");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_idchanged, disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_idchanged, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					or_get or = new or_get();
					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					String datetime = or_get.disc_chg_or_get(CNO, reg_idchanged, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_idchanged, datetime);

					deleteevent.delete();

				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			} else {
				childTest.log(Status.FAIL, "Could not retrieve a result set to chose a client to update");
				Assert.assertTrue(false, "Automation Test Error in Code");
			}
		} finally {
			s.executeQuery(prop.getProperty("reg_id_finallyupdate_onehalf") + "'" + reg_id + "'"
					+ prop.getProperty("reg_id_finallyupdate_secondhalf") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id" }, alwaysRun = true)
	public void sld_ynntoy() throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String Sld_yn = null;
		String Sld_ynchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {
			parentTest = extent.createTest(prop.getProperty("sld_ynntoy_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("sld_ynntoy_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("sld_ynntoy_oneselect"));

			disc_chgs: while (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				Sld_yn = rs.getString("sld_yn");

				if (Sld_yn.equals("N")) {
					String[][] requestchosen = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("syn"), prop.getProperty("dischrg_id") },
							{ CNO, clid, clid, Sld_yn, disc_chg_id } };
					m = MarkupHelper.createTable(requestchosen);
					childTest.log(Status.INFO, m);

					s.executeQuery(prop.getProperty("sld_ynntoy_oneupdate") + "" + disc_chg_id + "");
					conn.commit();

					extentreg_id = s.executeQuery(prop.getProperty("sld_ynntoy_twoselect") + "" + disc_chg_id + "");

					if (extentreg_id.next()) {
						CNO = extentreg_id.getString("CNO");
						clid = extentreg_id.getString("clid");
						Sld_ynchanged = extentreg_id.getString("sld_yn");
						disc_chg_id = extentreg_id.getString("disc_chg_id");
						TIMEST = extentreg_id.getString("TIMEST");

						String[][] changedrequest = {
								{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
										prop.getProperty("syn"), prop.getProperty("dischrg_id"), "UPD_DTM" },
								{ CNO, clid, reg_id, Sld_ynchanged, disc_chg_id, TIMEST } };
						m = MarkupHelper.createTable(changedrequest);
						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get or = new or_get();

						String datetime = or_get.disc_chg_or_get(CNO, reg_id, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

						deleteevent.delete();

						break disc_chgs;
					} else {
						childTest.log(Status.FAIL, "query error");
						Assert.assertTrue(false, "Automation Test Error in Code");
					}

				}

			}

		} finally {
			s.executeQuery(prop.getProperty("sld_ynntoy_finallyupdate") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy" }, alwaysRun = true)
	public void sld_ynyton() throws SQLException, IOException, ParseException, ClassNotFoundException {

		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String Sld_yn = null;
		String Sld_ynchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");

		try {
			parentTest = extent.createTest(prop.getProperty("sld_ynyton_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("reg_id_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			rs = s.executeQuery(prop.getProperty("sld_ynyton_oneselect"));

			disc_chgs: while (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				Sld_yn = rs.getString("sld_yn");

				if (Sld_yn.equals("Y")) {
					String[][] requestchosen = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
									prop.getProperty("syn"), prop.getProperty("dischrg_id") },
							{ CNO, clid, clid, Sld_yn, disc_chg_id } };
					m = MarkupHelper.createTable(requestchosen);
					childTest.log(Status.INFO, m);

					s.executeQuery(prop.getProperty("sld_ynyton_oneupdate") + "" + disc_chg_id + "");
					conn.commit();

					extentreg_id = s.executeQuery(prop.getProperty("sld_ynyton_twoselect") + "" + disc_chg_id + "");

					if (extentreg_id.next()) {
						CNO = extentreg_id.getString("cno");
						clid = extentreg_id.getString("clid");
						Sld_ynchanged = extentreg_id.getString("sld_yn");
						disc_chg_id = extentreg_id.getString("disc_chg_id");
						TIMEST = extentreg_id.getString("TIMEST");

						String[][] changedrequest = {
								{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
										prop.getProperty("syn"), prop.getProperty("dischrg_id"), "UPD_DTM" },
								{ CNO, clid, clid, Sld_yn, disc_chg_id, TIMEST } };
						m = MarkupHelper.createTable(changedrequest);
						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();

						String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						or_get or = new or_get();

						String datetime = or_get.disc_chg_or_get(CNO, reg_id, gd);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

						deleteevent.delete();

						break disc_chgs;
					} else {
						childTest.log(Status.FAIL, "query error");
						Assert.assertTrue(false, "Automation Test Error in Code");
					}

				}

			}

		} finally {
			s.executeQuery(prop.getProperty("sld_ynyton_finallyupdate") + "" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy", "sld_ynyton" }, alwaysRun = true)
	public void chg_dtchangedfromlast5yrstopast5yrs()
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();
		
		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String chg_dt = null;
		String chg_dtchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = prop.getProperty("insertanddeletedischarge_TYPE");
		String chgdt = null;
		String chgdtforchange = null;

	try {

			parentTest = extent.createTest(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_parent_extent"));
			
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));

			childTest = parentTest.createNode(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));

			rs = s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneselect"));

			disc_chgs: if (rs.next()) {
				CNO = rs.getString("cno");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				chg_dt = rs.getString("chg_dt");
				chgdt = birthdate(chg_dt);

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
					prop.getProperty("chg_dt"), prop.getProperty("dischrg_id") },
						{ CNO, clid, reg_id, chg_dt, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				chgdtforchange = birthdatechanged(chg_dt);

				s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneupdate_onehalf") + "'"
						+ chgdtforchange + "'"
						+ prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_oneupdate_twohalf") + ""
						+ disc_chg_id + "");
				conn.commit();

				extentreg_id = s.executeQuery(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_twoselect") + ""
						+ disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					chg_dtchanged = extentreg_id.getString("chg_dt");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
								prop.getProperty("chg_dt"), prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_id, chg_dtchanged,
									disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					or_get or = new or_get();

					String datetime = or.disc_chg_or_get(CNO, reg_id, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

					deleteevent.delete();

					break disc_chgs;
				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			}

		}finally {
			s.executeQuery("update cms_disciplinary_charges SET charge_dt='" + chgdt
					+ "' where disciplinary_charge_id=" + disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();
			s.close();
			

		}

	}

	@Test(dependsOnMethods = { "reg_id", "sld_ynntoy", "sld_ynyton" }, alwaysRun = true)
	public void charge_dtchangedpast5yrstolast5yrs()
			throws SQLException, IOException, ParseException, ClassNotFoundException {
		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet extentreg_id = null;

		String CNO = null;
		String clid;
		String reg_id = null;
		String chg_dt = null;
		String chg_dtchanged = null;
		String disc_chg_id = null;
		String TIMEST = null;
		String TYPE = "DISC_CHRG";
		String chgdt = null;
		String chgdtforchange = null;

		try {
			parentTest = extent.createTest(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_parent_extent"));
			parentTest.assignCategory(prop.getProperty("insertanddeletedischarge_TYPE"));
			tombstoneanddiscliplinarychargenotification n = new tombstoneanddiscliplinarychargenotification();
			Adapter da = new Adapter();

			childTest = parentTest.createNode(prop.getProperty("chg_dtchangedfromlast5yrstopast5yrs_onechild"));

			childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
			rs = s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_oneselect"));

			disc_chgs: if (rs.next()) {
				CNO = rs.getString("CNO");
				clid = rs.getString("clid");
				reg_id = rs.getString("reg_id");
				disc_chg_id = rs.getString("disc_chg_id");
				chg_dt = rs.getString("chg_dt");
				chgdt = birthdate(chg_dt);

				String[][] requestchosen = { { prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
					prop.getProperty("chg_dt"), prop.getProperty("dischrg_id")  },
						{ CNO, clid, reg_id, chg_dt, disc_chg_id } };
				m = MarkupHelper.createTable(requestchosen);
				childTest.log(Status.INFO, m);

				chgdtforchange = birthdatechanged(chg_dt);

				s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_oneupdate") + ""
						+ disc_chg_id + "");
				conn.commit();

				extentreg_id = s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_twoselect") + ""
						+ disc_chg_id + "");

				if (extentreg_id.next()) {
					CNO = extentreg_id.getString("CNO");
					clid = extentreg_id.getString("clid");
					chg_dtchanged = extentreg_id.getString("chg_dt");
					disc_chg_id = extentreg_id.getString("disc_chg_id");
					TIMEST = extentreg_id.getString("TIMEST");

					String[][] changedrequest = {
							{ prop.getProperty("CNO"), prop.getProperty("clno"), prop.getProperty("rd"),
								prop.getProperty("chg_dt"), prop.getProperty("dischrg_id"), "UPD_DTM" },
							{ CNO, clid, reg_id, chg_dtchanged,
									disc_chg_id, TIMEST } };
					m = MarkupHelper.createTable(changedrequest);
					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					tstone_Event_verify_event unprocessed = new tstone_Event_verify_event();

					String gd = unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, "NULL");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					or_get or = new or_get();

					String datetime = or.disc_chg_or_get(CNO, reg_id, gd);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_disc_chg();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying FETCHED_DATE in Unprocessed Response", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, CNO, reg_id, datetime);

					deleteevent.delete();

					break disc_chgs;
				} else {
					childTest.log(Status.FAIL, "query error");
					Assert.assertTrue(false, "Automation Test Error in Code");
				}

			}

		} finally {
			s.executeQuery(prop.getProperty("chg_dtchangedpast5yrstolast5yrs_finallyupdate_onehalf") + "'" + chgdt
					+ "'" + prop.getProperty("chg_dtchangedpast5yrstolast5yrs_finallyupdate_twohalf") + ""
					+ disc_chg_id + "");
			conn.commit();
			rs.close();
			extentreg_id.close();

			s.close();

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
>>>>>>> Sync with Master
=======
>>>>>>> Dev
