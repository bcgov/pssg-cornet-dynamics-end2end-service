package CTD_ContractTests;

import java.awt.Event;
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
import CTD_ContractTests.At_Event_verify_event;
import CTD_ContractTests.delete_events;
import CTD_ContractTests.notification;
import CTD_ContractTests.or_get;
import CTD_Extent_Report.extentreport;

public class at_event extends extentreport {

	public String username;
	public String password;
	public Connection conn;
	public FileInputStream fis;
	public Properties prop = new Properties();
	public String Filepath = System.getProperty("user.dir") + "/Properties/cornet.properties";
	public delete_events deleteevent = new delete_events();

	At_Event_verify_event unprocessed = new At_Event_verify_event();
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
	public void codeanddescriptionchanges() throws SQLException, ParseException, IOException, ClassNotFoundException {

		System.out.println(prop.getProperty("act_run_status"));

		Statement s = conn.createStatement();
		Statement f = conn.createStatement();

		Markup m;

		int count = 0;
		int maxtries = 2;

		True:
		while (true) {
			ResultSet rs = null;
			ResultSet rsup = null;
			ResultSet rsupcd = null;

			String code = null;
			String description = null;
			String TIMEST = null;
			String TYPE = prop.getProperty("act_TYPE");

			String gd = null;
			try {

				parentTest = extent.createTest(prop.getProperty("act_parent_test"));
				
				parentTest.assignCategory(prop.getProperty("at_event_category"));

				childTest = parentTest.createNode(prop.getProperty("act_child_onetest"));

				s.executeQuery(prop.getProperty("act_cdanddscchanges_first_insert"));
				conn.commit();

				rs = s.executeQuery(prop.getProperty("act_cdanddscchanges_first_select"));

				if (rs.next()) {
					code = rs.getString("code");
					description = rs.getString("description");
					TIMEST = rs.getString("TIMEST");

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
					String[][] requestchosen = { { "CODE", "DESC", "TIMEST" }, { code, description, TIMEST } };
					m = MarkupHelper.createTable(requestchosen);

					childTest.log(Status.INFO, m);

					childTest.log(Status.INFO,
							MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
					gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL", 1);
					childTest.log(Status.PASS,
							MarkupHelper.createLabel("An Event was triggered for the requested details", colour.GREEN));

					childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
					n.event_notification();

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
					String datetime = or_get.at_or_get(code, gd, description);

					childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
					da.da_at();

					childTest.log(Status.INFO, MarkupHelper
							.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
					unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime, 2);
					childTest.log(Status.PASS, MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

					deleteevent.delete();

					f.executeQuery(prop.getProperty("act_cdanddscchanges_updatingcd"));

					conn.commit();

					rsupcd = s.executeQuery(prop.getProperty("act_cdanddscchanges_updatingcd_and_select"));

					if (rsupcd.next()) {

						code = rsupcd.getString("code");
						description = rsupcd.getString("description");
						TIMEST = rsupcd.getString("TIMEST");

						childTest = parentTest.createNode(prop.getProperty("act_child_twotest"));

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
						childTest.log(Status.INFO, "The following was updated");
						String[][] cdchanged = { { "CODE", "DESC", "TIMEST" }, { code, description, TIMEST } };
						m = MarkupHelper.createTable(cdchanged);
						childTest.log(Status.INFO, m);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
						gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL", 1);
						childTest.log(Status.PASS, MarkupHelper
								.createLabel("An Event was triggered for the requested details", colour.GREEN));

						childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
						n.event_notification();

						childTest.log(Status.INFO, MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));
						datetime = or_get.at_or_get(code, gd, description);

						childTest.log(Status.INFO,
								MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
						da.da_at();
						
						childTest.log(Status.INFO, MarkupHelper
								.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
						unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime, 2);
						childTest.log(Status.PASS,
								MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

						deleteevent.delete();

						childTest = parentTest.createNode(prop.getProperty("act_child_threetest"));
						f.executeQuery(prop.getProperty("act_cdanddscchanges_updatingdsc"));
						conn.commit();

						rsup = s.executeQuery(prop.getProperty("act_cdanddscchanges_updatingcd_and_select"));

						if (rsup.next()) {
							TIMEST = rsup.getString("TIMEST");
							code = rsup.getString("code");
							description = rsup.getString("description");

							childTest.log(Status.INFO, MarkupHelper.createLabel("Step: Event Trigger", colour.BLUE));
							childTest.log(Status.INFO, "The desc was updated as");
							String[][] requestchanged = { { "CODE", "DESC", "UPD_DT" }, { code, description, TIMEST } };
							m = MarkupHelper.createTable(requestchanged);

							childTest.log(Status.INFO, m);

							childTest.log(Status.PASS,
									MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
							gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL", 1);
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("An Event was triggered for the requested details", colour.GREEN));

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
							n.event_notification();

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

							datetime = or_get.at_or_get(code, gd, description);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
							da.da_at();
							
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
							unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime, 2);
							childTest.log(Status.PASS,
									MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

							deleteevent.delete();

							childTest = parentTest.createNode(prop.getProperty("act_child_fourtest"));

							f.executeQuery(prop.getProperty("act_cdanddscchanges_delete"));
							conn.commit();

							childTest.log(Status.PASS,
									MarkupHelper.createLabel("Step: Verifying Unprocessed Response", colour.BLUE));
							gd = unprocessed.exampleGetTest(TIMEST, TYPE, code, "NULL", 1);
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("An Event was triggered for the requested details", colour.GREEN));

							childTest.log(Status.INFO,
									MarkupHelper.createLabel("Step: Event Notification", colour.BLUE));
							n.event_notification();

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("db_get"), colour.BLUE));

							datetime = or_get.at_or_get(code, gd, null);

							childTest.log(Status.INFO,
									MarkupHelper.createLabel(prop.getProperty("adapter_post"), colour.BLUE));
							da.da_at();
							
							childTest.log(Status.PASS, MarkupHelper
									.createLabel("Step: Verifying Unprocessed Response for Fetched Date", colour.BLUE));
							unprocessed.exampleGetTest(TIMEST, TYPE, code, datetime, 2);
							childTest.log(Status.PASS,
									MarkupHelper.createLabel("The Fetched_date was updated", colour.GREEN));

							deleteevent.delete();

							s.close();
							f.close();
							rs.close();
							rsup.close();
							rsupcd.close();
							
							break True;

						}

					}
				}

			} catch (SQLException e) {
				s.executeQuery(prop.getProperty("act_catch_delete"));
				conn.commit();
				s.executeQuery(prop.getProperty("act_catch_deletetheupdate"));
				conn.commit();
				deleteevent.delete();
				if (++count == maxtries)
					throw e;

			}
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
