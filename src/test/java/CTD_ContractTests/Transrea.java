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
import CTD_ContractTests.Transrea_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.inscscliefn;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Extent_Report.extentreport;

public class Transrea extends extentreport {
	public String username;
	public String password;
	public Connection conn;

	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	Transrea_Event_verify_event unprocessed = new Transrea_Event_verify_event();
	notification n = new notification();
	Adapter da = new Adapter();
	inscscliefn insc = new inscscliefn();

	@BeforeClass
	public void jdbc() throws SQLException, ClassNotFoundException {
		try {
			fis = new FileInputStream(Filepath);
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

	@Test
	public void cdchanges() throws SQLException, IOException, ParseException, ClassNotFoundException {

		Statement s = conn.createStatement();

		Markup m;

		ResultSet rs = null;
		ResultSet rsup = null;
		ResultSet rsupdsc = null;

		String code = null;
		String descr = null;
		String TIMEST = null;
		String TYPE = "TRANS_RSN";

		String gd = null;
		int count=0;
		int maxtries=2;

		try {
			parentTest = extent.createTest(prop.getProperty("Transrea_cdchanges_parent_extent"));
			childTest = parentTest.createNode("INSERT");
			parentTest.assignCategory("Trans_rea_category");

			System.out.println("..");

			exit: while (true) {
				try {
					s.executeQuery(prop.getProperty("Transrea_cdchanges_oneinsert"));
					conn.commit();

					rs = s.executeQuery(prop.getProperty("Transrea_cdchanges_oneselect"));
					if (rs.next()) {
						TIMEST = rs.getString("TIMEST");
						code = rs.getString("code");
						descr = rs.getString("descr");
						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was inserted");
						String[][] requestchosen = { { prop.getProperty("CNO"), "DESCR", "ENT_DT" },
								{ code, descr, TIMEST } };
						m = MarkupHelper.createTable(requestchosen);
						childTest.log(Status.INFO, m);

						childTest.log(Status.PASS,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL");
						childTest.log(Status.PASS, MarkupHelper
								.createLabel("An Event was triggered for the requested details", colour.GREEN));

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						String datetime = or_get.trans_rea_or_get(code, gd, descr);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.transferreason();

						childTest.log(Status.PASS, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

						deleteevent.delete();

						childTest = parentTest.createNode("UPDATE");
						s.executeQuery(prop.getProperty("Transrea_cdchanges_oneupdate"));
						conn.commit();

						rsup = s.executeQuery(prop.getProperty("Transrea_cdchanges_twoselect"));

						if (rsup.next()) {
							TIMEST = rsup.getString("TIMEST");
							code = rsup.getString("code");
							descr = rsup.getString("descr");
							childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
							childTest.log(Status.INFO, "The following was inserted");
							String[][] requestchanged = { { prop.getProperty("CNO"), "DESCR", "UPD_DT" },
									{ code, descr, TIMEST } };
							m = MarkupHelper.createTable(requestchanged);

							childTest.log(Status.INFO, m);

							childTest.log(Status.PASS,
									MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
							gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL");
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("An Event was triggered for the requested details", colour.GREEN));

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
							n.event_notification();

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

							datetime = or_get.trans_rea_or_get(code, gd, descr);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
							da.transferreason();

							childTest.log(Status.PASS, MarkupHelper
									.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
							unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime);
							childTest.log(Status.PASS,
									MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

							deleteevent.delete();

							childTest = parentTest.createNode("UPDATE DESCR");
							s.executeQuery(prop.getProperty("Transrea_cdchanges_twoupdate"));

							rsupdsc = s.executeQuery(prop.getProperty("Transrea_cdchanges_threeselect"));

							if (rsupdsc.next()) {
								TIMEST = rsupdsc.getString("TIMEST");
								code = rsupdsc.getString("code");
								descr = rsupdsc.getString("descr");
								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
								childTest.log(Status.INFO, "The following was inserted");
								String[][] requestchangeddescr = { { "CD", "DESCR", "UPD_DT" },
										{ code, descr, TIMEST } };
								m = MarkupHelper.createTable(requestchangeddescr);
								childTest.log(Status.INFO, m);
								childTest.log(Status.PASS,
										MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
								gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL");
								childTest.log(Status.PASS, MarkupHelper
										.createLabel("An Event was triggered for the requested details", colour.GREEN));

								childTest.log(Status.INFO,
										MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
								n.event_notification();

								childTest.log(Status.INFO,
										MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
								datetime = or_get.trans_rea_or_get(code, gd, descr);

								childTest.log(Status.INFO,
										MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
								da.transferreason();

								childTest.log(Status.PASS, MarkupHelper.createLabel(
										"Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
								unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime);
								childTest.log(Status.PASS,
										MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

								deleteevent.delete();

							}

						}

						childTest = parentTest.createNode("DELETE");
						s.executeQuery(prop.getProperty("Transrea_cdchanges_onedel"));
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL");
						childTest.log(Status.PASS, MarkupHelper
								.createLabel("An Event was triggered for the requested details", colour.GREEN));

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						datetime = or_get.trans_rea_or_get(code, gd, null);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.transferreason();

						childTest.log(Status.PASS, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

						deleteevent.delete();
					}
					break exit;

				} catch (SQLException e) {
					if(++count==maxtries)
						throw e;
					else
						s.executeQuery(prop.getProperty("transrea_cdchanges_catch_del"));
				}
			}
		} finally {
			s.close();
			rs.close();
			rsup.close();
			rsupdsc.close();

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
