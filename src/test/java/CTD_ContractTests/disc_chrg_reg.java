<<<<<<< master
package CTD_ContractTests;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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
import CTD_ContractTests.disc_chrg_reg_event_verify_event;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Extent_Report.extentreport;

public class disc_chrg_reg extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

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
	public void cms_regulations() throws ParseException, IOException, SQLException, ClassNotFoundException {

		Statement s = conn.createStatement();
		Statement f = conn.createStatement();
		ResultSet rs = null;
		ResultSet cdchanged = null;
		ResultSet descchanged = null;
		ResultSet eff_dtchanged = null;
		ResultSet exp_dtchanged = null;

		try {

			int count = 0;
			int maxtries = 2;

			Markup m;

			String cd = null;
			String desc = null;
			String TIMEST = null;
			String TYPE = prop.getProperty("disc_chg_reg_type");

			String gd = null;
			String eff_dt = null;
			String exp_dt = null;
			String use_adults_yn = null;
			String use_youths_yn = null;
			int id = 0;
			String idstring = null;

			exit:
			while (true) {
				try {
					parentTest = extent.createTest(prop.getProperty("disc_chg_reg_parent_extent"));
					parentTest.assignCategory(prop.getProperty("disc_chg_reg_category"));

					System.out.println("..");

					childTest = parentTest.createNode(prop.getProperty("disc_chg_reg_onechild"));
					disc_chrg_reg_event_verify_event unprocessed = new disc_chrg_reg_event_verify_event();
					notification n = new notification();
					Adapter da = new Adapter();

					s.executeQuery(prop.getProperty("cs_regu_oneinsert"));
					conn.commit();

					rs = s.executeQuery(prop.getProperty("cs_regu_oneselect"));

					if (rs.next()) {
						cd = rs.getString("code");
						desc = rs.getString("descr");
						TIMEST = rs.getString("TIMEST");
						eff_dt = rs.getString("eff_dt");
						exp_dt = rs.getString("exp_dt");
						use_adults_yn = rs.getString("use_adults_yn");
						use_youths_yn = rs.getString("use_youths_yn");
						id = rs.getInt("id");
						idstring = String.valueOf(id);

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
						String[][] requestchosen = {
								{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
										"use_youths_yn", "TIMEST" },
								{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
										TIMEST } };
						m = MarkupHelper.createTable(requestchosen);

						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
						childTest.log(Status.PASS, MarkupHelper
								.createLabel("An Event was triggered for the requested details", colour.GREEN));

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						String datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
								eff_dt, exp_dt);

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg_reg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));
						
						deleteevent.delete();

						childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_twochild"));

						f.executeQuery(prop.getProperty("cs_regu_oneupdate"));

						cdchanged = s.executeQuery(prop.getProperty("cs_regu_twoselect"));

						if (cdchanged.next()) {
							cd = cdchanged.getString("code");
							desc = cdchanged.getString("descr");
							TIMEST = cdchanged.getString("TIMEST");
							eff_dt = cdchanged.getString("eff_dt");
							exp_dt = cdchanged.getString("exp_dt");
							use_adults_yn = cdchanged.getString("use_adults_yn");
							use_youths_yn = cdchanged.getString("use_youths_yn");
							id = cdchanged.getInt("id");
							idstring = String.valueOf(id);
							
							childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
							childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
							String[][] cdchangedto = {
									{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
										"use_youths_yn", "TIMEST" },
								{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
										TIMEST } };
							m = MarkupHelper.createTable(cdchangedto);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
							gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("An Event was triggered for the requested details", colour.GREEN));

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
							n.event_notification();

							childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
							datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc, eff_dt,
									exp_dt);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
							da.da_disc_chg_reg();

							childTest.log(Status.INFO, MarkupHelper
									.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
							unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
							childTest.log(Status.PASS,
									MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

							deleteevent.delete();

							childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_threechild"));

							f.executeQuery(prop.getProperty("cs_regu_twoupdate"));

							descchanged = s.executeQuery(prop.getProperty("cs_regu_threeselect"));

							if (descchanged.next()) {
								cd = descchanged.getString("code");
								desc = descchanged.getString("descr");
								TIMEST = descchanged.getString("TIMEST");
								eff_dt = descchanged.getString("eff_dt");
								exp_dt = descchanged.getString("exp_dt");
								use_adults_yn = descchanged.getString("use_adults_yn");
								use_youths_yn = descchanged.getString("use_youths_yn");
								id = descchanged.getInt("id");
								idstring = String.valueOf(id);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
								childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
								String[][] dscchangedto = {
										{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
											"use_youths_yn", "TIMEST" },
									{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
											TIMEST } };
								m = MarkupHelper.createTable(dscchangedto);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
								gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
								childTest.log(Status.PASS, MarkupHelper
										.createLabel("An Event was triggered for the requested details", colour.GREEN));

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
								n.event_notification();

								childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
								datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
										eff_dt, exp_dt);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
								da.da_disc_chg_reg();

								childTest.log(Status.INFO, MarkupHelper.createLabel(
										"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
								unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
								childTest.log(Status.PASS,
										MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

								deleteevent.delete();
								
								childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_fourchild"));

								f.executeQuery(prop.getProperty("cs_regu_threeupdate"));

								eff_dtchanged = s.executeQuery(prop.getProperty("cs_regu_fourselect"));

								if (eff_dtchanged.next()) {
									cd = eff_dtchanged.getString("code");
									desc = eff_dtchanged.getString("descr");
									TIMEST = eff_dtchanged.getString("TIMEST");
									eff_dt = eff_dtchanged.getString("eff_dt");
									exp_dt = eff_dtchanged.getString("exp_dt");
									use_adults_yn = eff_dtchanged.getString("use_adults_yn");
									use_youths_yn = eff_dtchanged.getString("use_youths_yn");
									id = eff_dtchanged.getInt("id");
									idstring = String.valueOf(id);

									childTest.log(Status.INFO,
											MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
									childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
									String[][] effective_dtchangedto = {
											{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
												"use_youths_yn", "TIMEST" },
										{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
												TIMEST } };
									m = MarkupHelper.createTable(effective_dtchangedto);

									childTest.log(Status.INFO, MarkupHelper
											.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
									gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
									childTest.log(Status.PASS, MarkupHelper.createLabel(
											"An Event was triggered for the requested details", colour.GREEN));

									childTest.log(Status.INFO,
											MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
									n.event_notification();

									childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
									datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
											eff_dt, exp_dt);

									childTest.log(Status.INFO,
											MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
									da.da_disc_chg_reg();

									childTest.log(Status.INFO, MarkupHelper.createLabel(
											"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
									unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
									childTest.log(Status.PASS,
											MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

									deleteevent.delete();

									childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_fivechild"));

									f.executeQuery(prop.getProperty("cs_regu_fourupdate"));

									exp_dtchanged = s.executeQuery(prop.getProperty("cs_regu_fiveselect"));

									if (exp_dtchanged.next()) {
										cd = exp_dtchanged.getString("code");
										desc = exp_dtchanged.getString("descr");
										TIMEST = exp_dtchanged.getString("TIMEST");
										eff_dt = exp_dtchanged.getString("eff_dt");
										exp_dt = exp_dtchanged.getString("exp_dt");
										use_adults_yn = exp_dtchanged.getString("use_adults_yn");
										use_youths_yn = exp_dtchanged.getString("use_youths_yn");
										id = exp_dtchanged.getInt("id");
										idstring = String.valueOf(id);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
										childTest.log(Status.INFO,
												"The following was inserted into cms_move_reason_types");
										String[][] expiry_dtchangedto = {
												{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
													"use_youths_yn", "TIMEST" },
											{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
													TIMEST } };
										m = MarkupHelper.createTable(expiry_dtchangedto);

										childTest.log(Status.INFO, MarkupHelper
												.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
										gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
										childTest.log(Status.PASS, MarkupHelper.createLabel(
												"An Event was triggered for the requested details", colour.GREEN));

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
										n.event_notification();

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
										datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
												eff_dt, exp_dt);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
										da.da_disc_chg_reg();

										childTest.log(Status.INFO, MarkupHelper.createLabel(
												"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
										unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
										childTest.log(Status.PASS,
												MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

										deleteevent.delete();

										childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_sixchild"));

										f.executeQuery(prop.getProperty("cs_regu_onedelete"));

										childTest.log(Status.INFO, MarkupHelper
												.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
										gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
										childTest.log(Status.PASS, MarkupHelper.createLabel(
												"An Event was triggered for the requested details", colour.GREEN));

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
										n.event_notification();

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
										datetime = or_get.disc_chg_reg_or_get(id, null, gd, null,
												null, null);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
										da.da_disc_chg_reg();

										childTest.log(Status.INFO, MarkupHelper.createLabel(
												"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
										unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
										childTest.log(Status.PASS,
												MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

										deleteevent.delete();
										
										break exit;

									}
								}

							}

						}

					}
				} catch (SQLException e) {
					s.executeQuery("delete from cms_regulations where cd in ('21VNZ', '21AZ' )");
					conn.commit();
					if (++count == maxtries)
						throw e;
				}
			}
		} finally {
			rs.close();
			s.close();
			f.close();
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
import CTD_ContractTests.disc_chrg_reg_event_verify_event;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Extent_Report.extentreport;

public class disc_chrg_reg extends extentreport {

	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

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
	public void cms_regulations() throws ParseException, IOException, SQLException, ClassNotFoundException {

		Statement s = conn.createStatement();
		Statement f = conn.createStatement();
		ResultSet rs = null;
		ResultSet cdchanged = null;
		ResultSet descchanged = null;
		ResultSet eff_dtchanged = null;
		ResultSet exp_dtchanged = null;

		try {

			int count = 0;
			int maxtries = 2;

			Markup m;

			String cd = null;
			String desc = null;
			String TIMEST = null;
			String TYPE = prop.getProperty("disc_chg_reg_type");

			String gd = null;
			String eff_dt = null;
			String exp_dt = null;
			String use_adults_yn = null;
			String use_youths_yn = null;
			int id = 0;
			String idstring = null;

			exit:
			while (true) {
				try {
					parentTest = extent.createTest(prop.getProperty("disc_chg_reg_parent_extent"));
					parentTest.assignCategory(prop.getProperty("disc_chg_reg_category"));

					System.out.println("..");

					childTest = parentTest.createNode(prop.getProperty("disc_chg_reg_onechild"));
					disc_chrg_reg_event_verify_event unprocessed = new disc_chrg_reg_event_verify_event();
					notification n = new notification();
					Adapter da = new Adapter();

					s.executeQuery(prop.getProperty("cs_regu_oneinsert"));
					conn.commit();

					rs = s.executeQuery(prop.getProperty("cs_regu_oneselect"));

					if (rs.next()) {
						cd = rs.getString("code");
						desc = rs.getString("descr");
						TIMEST = rs.getString("TIMEST");
						eff_dt = rs.getString("eff_dt");
						exp_dt = rs.getString("exp_dt");
						use_adults_yn = rs.getString("use_adults_yn");
						use_youths_yn = rs.getString("use_youths_yn");
						id = rs.getInt("id");
						idstring = String.valueOf(id);

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
						String[][] requestchosen = {
								{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
										"use_youths_yn", "TIMEST" },
								{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
										TIMEST } };
						m = MarkupHelper.createTable(requestchosen);

						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
						childTest.log(Status.PASS, MarkupHelper
								.createLabel("An Event was triggered for the requested details", colour.GREEN));

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						String datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
								eff_dt, exp_dt);

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_disc_chg_reg();

						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));
						
						deleteevent.delete();

						childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_twochild"));

						f.executeQuery(prop.getProperty("cs_regu_oneupdate"));

						cdchanged = s.executeQuery(prop.getProperty("cs_regu_twoselect"));

						if (cdchanged.next()) {
							cd = cdchanged.getString("code");
							desc = cdchanged.getString("descr");
							TIMEST = cdchanged.getString("TIMEST");
							eff_dt = cdchanged.getString("eff_dt");
							exp_dt = cdchanged.getString("exp_dt");
							use_adults_yn = cdchanged.getString("use_adults_yn");
							use_youths_yn = cdchanged.getString("use_youths_yn");
							id = cdchanged.getInt("id");
							idstring = String.valueOf(id);
							
							childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
							childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
							String[][] cdchangedto = {
									{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
										"use_youths_yn", "TIMEST" },
								{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
										TIMEST } };
							m = MarkupHelper.createTable(cdchangedto);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
							gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("An Event was triggered for the requested details", colour.GREEN));

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
							n.event_notification();

							childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
							datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc, eff_dt,
									exp_dt);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
							da.da_disc_chg_reg();

							childTest.log(Status.INFO, MarkupHelper
									.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
							unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
							childTest.log(Status.PASS,
									MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

							deleteevent.delete();

							childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_threechild"));

							f.executeQuery(prop.getProperty("cs_regu_twoupdate"));

							descchanged = s.executeQuery(prop.getProperty("cs_regu_threeselect"));

							if (descchanged.next()) {
								cd = descchanged.getString("code");
								desc = descchanged.getString("descr");
								TIMEST = descchanged.getString("TIMEST");
								eff_dt = descchanged.getString("eff_dt");
								exp_dt = descchanged.getString("exp_dt");
								use_adults_yn = descchanged.getString("use_adults_yn");
								use_youths_yn = descchanged.getString("use_youths_yn");
								id = descchanged.getInt("id");
								idstring = String.valueOf(id);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
								childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
								String[][] dscchangedto = {
										{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
											"use_youths_yn", "TIMEST" },
									{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
											TIMEST } };
								m = MarkupHelper.createTable(dscchangedto);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
								gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
								childTest.log(Status.PASS, MarkupHelper
										.createLabel("An Event was triggered for the requested details", colour.GREEN));

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
								n.event_notification();

								childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
								datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
										eff_dt, exp_dt);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
								da.da_disc_chg_reg();

								childTest.log(Status.INFO, MarkupHelper.createLabel(
										"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
								unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
								childTest.log(Status.PASS,
										MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

								deleteevent.delete();
								
								childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_fourchild"));

								f.executeQuery(prop.getProperty("cs_regu_threeupdate"));

								eff_dtchanged = s.executeQuery(prop.getProperty("cs_regu_fourselect"));

								if (eff_dtchanged.next()) {
									cd = eff_dtchanged.getString("code");
									desc = eff_dtchanged.getString("descr");
									TIMEST = eff_dtchanged.getString("TIMEST");
									eff_dt = eff_dtchanged.getString("eff_dt");
									exp_dt = eff_dtchanged.getString("exp_dt");
									use_adults_yn = eff_dtchanged.getString("use_adults_yn");
									use_youths_yn = eff_dtchanged.getString("use_youths_yn");
									id = eff_dtchanged.getInt("id");
									idstring = String.valueOf(id);

									childTest.log(Status.INFO,
											MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
									childTest.log(Status.INFO, "The following was inserted into cms_move_reason_types");
									String[][] effective_dtchangedto = {
											{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
												"use_youths_yn", "TIMEST" },
										{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
												TIMEST } };
									m = MarkupHelper.createTable(effective_dtchangedto);

									childTest.log(Status.INFO, MarkupHelper
											.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
									gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
									childTest.log(Status.PASS, MarkupHelper.createLabel(
											"An Event was triggered for the requested details", colour.GREEN));

									childTest.log(Status.INFO,
											MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
									n.event_notification();

									childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
									datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
											eff_dt, exp_dt);

									childTest.log(Status.INFO,
											MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
									da.da_disc_chg_reg();

									childTest.log(Status.INFO, MarkupHelper.createLabel(
											"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
									unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
									childTest.log(Status.PASS,
											MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

									deleteevent.delete();

									childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_fivechild"));

									f.executeQuery(prop.getProperty("cs_regu_fourupdate"));

									exp_dtchanged = s.executeQuery(prop.getProperty("cs_regu_fiveselect"));

									if (exp_dtchanged.next()) {
										cd = exp_dtchanged.getString("code");
										desc = exp_dtchanged.getString("descr");
										TIMEST = exp_dtchanged.getString("TIMEST");
										eff_dt = exp_dtchanged.getString("eff_dt");
										exp_dt = exp_dtchanged.getString("exp_dt");
										use_adults_yn = exp_dtchanged.getString("use_adults_yn");
										use_youths_yn = exp_dtchanged.getString("use_youths_yn");
										id = exp_dtchanged.getInt("id");
										idstring = String.valueOf(id);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
										childTest.log(Status.INFO,
												"The following was inserted into cms_move_reason_types");
										String[][] expiry_dtchangedto = {
												{ "ID", "CODE", "DESC", "eff_dt", "exp_dt", "use_adults_yn",
													"use_youths_yn", "TIMEST" },
											{ idstring, cd, desc, eff_dt, exp_dt, use_adults_yn, use_youths_yn,
													TIMEST } };
										m = MarkupHelper.createTable(expiry_dtchangedto);

										childTest.log(Status.INFO, MarkupHelper
												.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
										gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
										childTest.log(Status.PASS, MarkupHelper.createLabel(
												"An Event was triggered for the requested details", colour.GREEN));

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
										n.event_notification();

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
										datetime = or_get.disc_chg_reg_or_get(id, cd, gd, desc,
												eff_dt, exp_dt);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
										da.da_disc_chg_reg();

										childTest.log(Status.INFO, MarkupHelper.createLabel(
												"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
										unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
										childTest.log(Status.PASS,
												MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

										deleteevent.delete();

										childTest = parentTest.createNode(prop.getProperty("disc_chrg_reg_sixchild"));

										f.executeQuery(prop.getProperty("cs_regu_onedelete"));

										childTest.log(Status.INFO, MarkupHelper
												.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
										gd = unprocessed.exampleGetTest(TIMEST, TYPE, idstring, "NULL", 1);
										childTest.log(Status.PASS, MarkupHelper.createLabel(
												"An Event was triggered for the requested details", colour.GREEN));

										childTest.log(Status.INFO,
												MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
										n.event_notification();

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
										datetime = or_get.disc_chg_reg_or_get(id, null, gd, null,
												null, null);

										childTest.log(Status.INFO,
												MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
										da.da_disc_chg_reg();

										childTest.log(Status.INFO, MarkupHelper.createLabel(
												"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
										unprocessed.exampleGetTest(TIMEST, TYPE, idstring, datetime, 2);
										childTest.log(Status.PASS,
												MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

										deleteevent.delete();
										
										break exit;

									}
								}

							}

						}

					}
				} catch (SQLException e) {
					s.executeQuery("delete from cms_regulations where cd in ('21VNZ', '21AZ' )");
					conn.commit();
					if (++count == maxtries)
						throw e;
				}
			}
		} finally {
			rs.close();
			s.close();
			f.close();
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
